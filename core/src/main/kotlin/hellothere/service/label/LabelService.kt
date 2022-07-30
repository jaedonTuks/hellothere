package hellothere.service.label

import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.BatchModifyMessagesRequest
import hellothere.dto.label.LabelDto
import hellothere.dto.label.LabelUpdateDto
import hellothere.model.email.UserEmail
import hellothere.model.label.UserLabel
import hellothere.model.label.UserLabelId
import hellothere.repository.email.UserEmailRepository
import hellothere.repository.label.UserLabelRepository
import hellothere.repository.user.UserRepository
import hellothere.requests.label.UpdateLabelsRequest
import hellothere.service.google.GmailService.Companion.USER_SELF_ACCESS
import liquibase.pro.packaged.it
import org.slf4j.Logger
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LabelService(
    private val userRepository: UserRepository,
    private val userLabelRepository: UserLabelRepository,
    private val userEmailRepository: UserEmailRepository,
) {

    @Transactional
    fun getAllUserLabels(username: String): List<UserLabel> {
        return userLabelRepository.findAllByUserId(username)
    }

    @Transactional
    fun getLabels(client: Gmail, username: String): List<LabelDto> {
        val labelIds = getLabelIds(client, username)

        val cachedLabels = getCachedLabels(labelIds, username)

        val labelIdsToFetch = labelIds.filter { labelId ->
            cachedLabels.none { cachedLabel -> labelId != cachedLabel.id.gmailId }
        }

        val newLabels = fetchAndCacheLabels(labelIdsToFetch, client, username)

        return (newLabels + cachedLabels).map {
            buildLabelDto(it)
        }
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

        val labels = labelIds.map {
            client.users()
                .Labels()
                .get(USER_SELF_ACCESS, it)
                .execute()
        }

        val labelsToSave = labels.map {
            UserLabel(
                UserLabelId(it.id, user.id),
                it.name,
                it.threadsUnread,
                user
            )
        }

        return userLabelRepository.saveAll(labelsToSave)
    }

    fun getLabelIds(client: Gmail, username: String): List<String> {
        val labelResponse = client
            .users()
            .Labels()
            .list(USER_SELF_ACCESS)
            .execute()

        return labelResponse.labels.map { it.id }
    }

    fun getCachedLabels(labelIds: List<String>, username: String): List<UserLabel> {
        return userLabelRepository.findAllByIdIn(labelIds.map { UserLabelId(it, username) })
    }

    fun buildLabelDto(label: UserLabel): LabelDto {
        return LabelDto(
            label.name,
            label.unreadThreads
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

        val messagesToModify = userEmailRepository.findAllByThreadThreadIdInAndThreadUserId(
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

        LOGGER.info("Finished adding labels $labelsToAdd and removing $labelsToRemove for threads: $threadIds - user: $username")

        val labelsPerThread = updatedMessages
            .filter { it.thread?.threadId != null }
            .groupBy { it.thread!!.threadId }
            .mapValues { mapEntry -> mapEntry.value.flatMap { it.getLabelList() }.toSet() }

        val allUserLabels = userLabelRepository.findAllByUserId(username)

        return LabelUpdateDto(
            labelsPerThread,
            allUserLabels.map { buildLabelDto(it) }
        )
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
        updateLabelRequest: UpdateLabelsRequest
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
        val cachedAddLabels = getCachedLabelsByNameAndUser(labelsToAdd, username)
        val cachedRemoveLabels = getCachedLabelsByNameAndUser(labelsToRemove, username)

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

    private fun getCachedLabelsByNameAndUser(labels: List<String>, username: String): List<UserLabel> {
        return userLabelRepository.findAllByUserIdAndNameInIgnoreCase(username, labels)
    }

    companion object {
        val LOGGER: Logger = org.slf4j.LoggerFactory.getLogger(LabelService::class.java)
    }
}
