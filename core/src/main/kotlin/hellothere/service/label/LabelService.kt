package hellothere.service.label

import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.ModifyThreadRequest
import hellothere.dto.label.LabelDto
import hellothere.model.email.EmailThread
import hellothere.model.label.UserLabel
import hellothere.repository.label.UserLabelRepository
import hellothere.repository.user.UserRepository
import hellothere.service.google.GmailService.Companion.USER_SELF_ACCESS
import org.slf4j.Logger
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LabelService(
    private val userRepository: UserRepository,
    private val userLabelRepository: UserLabelRepository
) {
    @Transactional
    fun getLabels(client: Gmail, username: String): List<LabelDto> {
        val labelIds = getLabelIds(client, username)

        val cachedLabels = getCachedLabels(labelIds)

        val labelIdsToFetch = labelIds.filter { labelId ->
            cachedLabels.none { cachedLabel -> labelId != cachedLabel.gmailId }
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
                null,
                it.id,
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

    fun getCachedLabels(labelIds: List<String>): List<UserLabel> {
        return userLabelRepository.findAllByGmailIdIn(labelIds)
    }

    fun buildLabelDto(label: UserLabel): LabelDto {
        return LabelDto(
            label.name,
            label.unreadThreads
        )
    }

    @Transactional
    fun setThreadLabels(username: String, client: Gmail, thread: EmailThread, labels: List<String>) {
        val cachedLabels = getCachedLabelsByNameAndUser(labels, username)
        // todo make it possible to add extra labels here that werent cached
        if (cachedLabels.isEmpty()) {
            LOGGER.info("Skipping adding labels to thread")
            return
        }

        val modifyThreadRequest = ModifyThreadRequest()
        modifyThreadRequest.addLabelIds = cachedLabels.map { it.gmailId }
        client.users()
            .threads()
            .modify(USER_SELF_ACCESS, thread.threadId, modifyThreadRequest)
            .execute()

        LOGGER.info("Setting labels [$labels] for thread: ${thread.threadId} - user: $username")
    }

    private fun getCachedLabelsByNameAndUser(labels: List<String>, username: String): List<UserLabel> {
        return userLabelRepository.findAllByUserIdAndNameInIgnoreCase(username, labels)
    }

    companion object {
        val LOGGER: Logger = org.slf4j.LoggerFactory.getLogger(LabelService::class.java)
    }
}
