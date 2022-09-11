package hellothere.service.label

import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.BatchModifyMessagesRequest
import com.google.api.services.gmail.model.Label
import hellothere.dto.label.LabelContainerDTO
import hellothere.dto.label.LabelDto
import hellothere.dto.label.LabelUpdateDto
import hellothere.model.email.UserEmail
import hellothere.model.feature.FF4jFeature
import hellothere.model.label.UserLabel
import hellothere.model.label.UserLabel.Companion.isManageableId
import hellothere.model.label.UserLabelId
import hellothere.model.stats.category.StatCategory
import hellothere.repository.email.UserEmailRepository
import hellothere.repository.label.UserLabelRepository
import hellothere.repository.user.UserRepository
import hellothere.requests.label.UpdateEmailLabelsRequest
import hellothere.requests.label.UpdateLabelColorRequest
import hellothere.requests.label.UpdateLabelViewableRequest
import hellothere.service.FeatureService
import hellothere.service.challenge.ChallengeService
import hellothere.service.google.BatchCallbacks.LabelBatchCallback
import hellothere.service.google.GmailService.Companion.USER_SELF_ACCESS
import hellothere.service.user.UserStatsService
import org.slf4j.Logger
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LabelService(
    private val userRepository: UserRepository,
    private val userLabelRepository: UserLabelRepository,
    private val userEmailRepository: UserEmailRepository,
    private val userStatsService: UserStatsService,
    private val featureService: FeatureService,
    private val challengeService: ChallengeService
) {

    @Transactional
    fun getAllUserLabels(username: String): List<UserLabel> {
        return userLabelRepository.findAllByUserId(username)
    }

    @Transactional
    fun getLabels(client: Gmail, username: String): LabelContainerDTO {
        val labelIds = getLabelIds(client, username)

        val cachedLabels = getCachedLabels(labelIds, username)

        val labelIdsToFetch = labelIds.filter { labelId ->
            cachedLabels.none { cachedLabel -> labelId == cachedLabel.id.gmailId }
        }

        val newLabels = fetchAndCacheLabels(labelIdsToFetch, client, username)

        val labels = (newLabels + cachedLabels).sortedBy { it.name }.filter { !it.name.contains("CATEGORY_") }.map {
            buildLabelDto(it)
        }
        val colors = challengeService.getUnlockedColors(username)
        return LabelContainerDTO(
            labels,
            colors
        )
    }

    private fun fetchAndCacheLabels(labelIds: List<String>, client: Gmail, username: String): List<UserLabel> {
        if (labelIds.isEmpty()) {
            LOGGER.info("All user labels already cached. Skipping cache request for $username")
            return listOf()
        }
        val user = userRepository.findByIdOrNull(username)

        if (user == null) {
            LOGGER.warn("No user found for username $username. ")
            return listOf()
        }
        val labels = mutableListOf<Label>()

        val batchRequest = client.batch()
        val callback = LabelBatchCallback(labels)

        labelIds.forEach { labelId ->
            client.users()
                .Labels()
                .get(USER_SELF_ACCESS, labelId)
                .queue(batchRequest, callback)
        }
        batchRequest.execute()

        val labelsToSave = labels.map {
            UserLabel(
                UserLabelId(it.id, user.id),
                it.name.replace("CATEGORY_", ""),
                "#FFF",
                it.threadsUnread,
                isManageableId(it.id),
                true,
                user
            )
        }

        return userLabelRepository.saveAll(labelsToSave)
    }

    fun getLabelIdsByName(labelNames: List<String>, username: String): List<String> {
        return getCachedLabelsByNameAndUser(labelNames, username).map {
            it.id.gmailId
        }
    }

    fun getLabelIds(client: Gmail, username: String): List<String> {
        val labelResponse = client
            .users()
            .Labels()
            .list(USER_SELF_ACCESS)
            .execute()

        return labelResponse.labels.map { it.id }
    }

    fun getUnreadMessageCount(client: Gmail, username: String): Int {
        val unreadLabelResponse = client
            .users()
            .Labels()
            .get(USER_SELF_ACCESS, "INBOX")
            .execute()

        return unreadLabelResponse.messagesUnread
    }

    fun getCachedLabels(labelIds: List<String>, username: String): List<UserLabel> {
        return userLabelRepository.findAllByIdIn(labelIds.map { UserLabelId(it, username) })
    }

    fun buildLabelDto(label: UserLabel): LabelDto {
        return LabelDto(
            label.id.gmailId,
            label.name,
            label.unreadThreads,
            label.color,
            label.isManageable,
            label.isViewable
        )
    }

    @Transactional
    fun batchUpdateLabels(
        username: String,
        client: Gmail,
        threadIds: List<String>,
        labelsToAdd: List<String> = listOf(),
        labelsToRemove: List<String> = listOf()
    ): LabelUpdateDto? {
        val (cachedAddLabels, cachedRemoveLabels) = getCachedLabels(labelsToAdd, labelsToRemove, username)
            ?: return null

        val messagesToModify = userEmailRepository.findAllByThreadIdThreadIdInAndThreadUserIdOrderByDateSent(
            threadIds,
            username
        )
        // todo verify that all emails actually have the values to add or remove?

        val batchModifyThreadRequest = BatchModifyMessagesRequest()
        batchModifyThreadRequest.ids = messagesToModify.map { it.gmailId }
        batchModifyThreadRequest.addLabelIds = cachedAddLabels.map { it.id.gmailId }
        batchModifyThreadRequest.removeLabelIds = cachedRemoveLabels.map { it.id.gmailId }

        client.users()
            .messages()
            .batchModify(USER_SELF_ACCESS, batchModifyThreadRequest)
            .execute()

        val updatedMessages = updateLabelCache(messagesToModify, cachedAddLabels, cachedRemoveLabels)
        val category = if (cachedRemoveLabels.map { it.id.gmailId }.contains("UNREAD")) {
            StatCategory.READ
        } else {
            StatCategory.LABEL
        }

        userStatsService.updateUserStats(username, category, updatedMessages)

        LOGGER.info("Finished adding labels $labelsToAdd and removing $labelsToRemove for threads: $threadIds - user: $username")

        val labelsPerThread = updatedMessages
            .filter { it.thread?.id?.threadId != null }
            .groupBy { it.thread!!.id.threadId }
            .mapValues { mapEntry -> mapEntry.value.flatMap { it.getLabelList() }.toSet() }

        val allUserLabels = userLabelRepository.findAllByUserId(username)

        return LabelUpdateDto(
            labelsPerThread,
            allUserLabels.map { buildLabelDto(it) }
        )
    }

    @Transactional
    fun updateLabelIsViewable(
        username: String,
        updateLabelRequest: UpdateLabelViewableRequest
    ): LabelDto? {
        if (featureService.isDisabled(FF4jFeature.CUSTOMISATION)) {
            return null
        }

        val label = userLabelRepository.findByIdOrNull(UserLabelId(updateLabelRequest.labelId, username))

        label?.isViewable = updateLabelRequest.isViewable

        return label?.let { buildLabelDto(userLabelRepository.save(it)) }
    }
    @Transactional
    fun updateLabelColor(
        username: String,
        updateLabelRequest: UpdateLabelColorRequest
    ): LabelDto? {
        if (featureService.isDisabled(FF4jFeature.CUSTOMISATION)) {
            return null
        }

        val availableColors = challengeService.getUnlockedColors(username)

        if (!availableColors.contains(updateLabelRequest.color)) {
            LOGGER.warn("Unable to update label ${updateLabelRequest.labelId} with color: ${updateLabelRequest.color} for user $username, color not yet unlocked.")
            return null
        }

        val label = userLabelRepository.findByIdOrNull(UserLabelId(updateLabelRequest.labelId, username))

        label?.color = updateLabelRequest.color

        return label?.let { buildLabelDto(userLabelRepository.save(it)) }
    }

    fun updateLabels(
        username: String,
        client: Gmail,
        threadId: String,
        labelsToAdd: List<String> = listOf(),
        labelsToRemove: List<String> = listOf()
    ): LabelUpdateDto? {
        return batchUpdateLabels(
            username,
            client,
            listOf(threadId),
            labelsToAdd,
            labelsToRemove
        )
    }

    fun updateLabels(
        username: String,
        client: Gmail,
        updateLabelRequest: UpdateEmailLabelsRequest
    ): LabelUpdateDto? {
        return batchUpdateLabels(
            username,
            client,
            updateLabelRequest.threadIds,
            updateLabelRequest.addLabels,
            updateLabelRequest.removeLabels
        )
    }

    @Transactional
    protected fun updateLabelCache(
        allThreadMessages: List<UserEmail>,
        cachedAddLabels: List<UserLabel>,
        cachedRemoveLabels: List<UserLabel>
    ): List<UserEmail> {
        allThreadMessages.forEach {
            it.addAllLabels(cachedAddLabels)
            it.removeAllLabels(cachedRemoveLabels)
        }

        return userEmailRepository.saveAll(allThreadMessages)
    }

    @Transactional
    protected fun getCachedLabels(
        labelsToAdd: List<String>,
        labelsToRemove: List<String>,
        username: String
    ): Pair<List<UserLabel>, List<UserLabel>>? {
        val cachedAddLabels = getCachedLabelsByIdAndUser(labelsToAdd, username)
        val cachedRemoveLabels = getCachedLabelsByIdAndUser(labelsToRemove, username)

        if (cachedAddLabels.isEmpty()) {
            LOGGER.info("Skipping adding labels to thread")
        }

        if (cachedRemoveLabels.isEmpty()) {
            LOGGER.info("Skipping removing labels to thread")
        }

        if (cachedAddLabels.isEmpty() && cachedRemoveLabels.isEmpty()) {
            return null
        }

        return Pair(cachedAddLabels, cachedRemoveLabels)
    }

    private fun getCachedLabelsByNameAndUser(labelNames: List<String>, username: String): List<UserLabel> {
        return userLabelRepository.findAllByUserIdAndNameInIgnoreCase(username, labelNames)
    }

    private fun getCachedLabelsByIdAndUser(labelIds: List<String>, username: String): List<UserLabel> {
        return userLabelRepository.findAllByUserIdAndIdGmailIdInIgnoreCase(username, labelIds)
    }

    companion object {
        val LOGGER: Logger = org.slf4j.LoggerFactory.getLogger(LabelService::class.java)
    }
}
