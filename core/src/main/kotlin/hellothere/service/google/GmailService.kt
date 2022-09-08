package hellothere.service.google

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Message
import hellothere.dto.email.AttachmentDTO
import hellothere.dto.email.EmailDto
import hellothere.dto.email.EmailThreadDto
import hellothere.dto.email.EmailsContainerDTO
import hellothere.model.email.EmailFormat
import hellothere.model.email.EmailHeaderName
import hellothere.model.email.EmailThread
import hellothere.model.email.UserEmail
import hellothere.model.email.ids.EmailThreadId
import hellothere.model.stats.category.StatCategory
import hellothere.model.user.UserAccessToken
import hellothere.repository.email.EmailThreadRepository
import hellothere.repository.email.UserEmailRepository
import hellothere.requests.email.ReplyRequest
import hellothere.requests.email.SendRequest
import hellothere.service.ConversionService
import hellothere.service.google.BatchCallbacks.MessageBatchCallback
import hellothere.service.google.BatchCallbacks.ThreadBatchCallback
import hellothere.service.label.LabelService
import hellothere.service.user.UserService
import hellothere.service.user.UserStatsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*
import javax.mail.Session
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Service
class GmailService(
    @Value("\${gmail.client.projectId}") private val projectId: String,
    @Value("\${max-emails}") private val maxEmails: Long,
    private val userEmailRepository: UserEmailRepository,
    private val emailThreadRepository: EmailThreadRepository,
    private val googleAuthenticationService: GoogleAuthenticationService,
    private val userService: UserService,
    private val userStatsService: UserStatsService,
    private val conversionService: ConversionService,
    private val labelService: LabelService
) {
    fun getGmailClientFromCredentials(credentials: Credential): Gmail {
        return Gmail.Builder(
            googleAuthenticationService.httpTransport,
            googleAuthenticationService.jsonFactory,
            credentials
        ).setApplicationName(projectId)
            .build()
    }

    fun getGmailClientFromUsername(username: String): Gmail? {
        return googleAuthenticationService.getUserCredentialFromUsername(username)?.let {
            getGmailClientFromCredentials(it)
        }
    }

    fun getGmailClientFromCode(code: String): Pair<Gmail, UserAccessToken> {
        val (credentials, userAccessToken) = googleAuthenticationService
            .createAndStoreCredential(code)

        return Pair(getGmailClientFromCredentials(credentials), userAccessToken)
    }

    fun getEmailThreadIdList(
        client: Gmail,
        query: String? = null,
        pageToken: String? = null,
        labelIds: List<String> = listOf()
    ): Pair<String?, List<String>> {
        LOGGER.info("Fetching email ids with search query: {$query} and ids {$labelIds}")

        val messageList = client
            .users()
            .threads()
            .list(USER_SELF_ACCESS)
            .setMaxResults(maxEmails)

        if (!query.isNullOrBlank()) {
            messageList.q = query
        }

        if (!pageToken.isNullOrBlank()) {
            messageList.pageToken = pageToken
        }

        if (labelIds.isNotEmpty()) {
            messageList.labelIds = labelIds
        }

        val result = messageList
            .execute()

        if (result.resultSizeEstimate == 0L) {
            return Pair(null, listOf())
        }

        return Pair(result.nextPageToken, result.threads.map { it.id })
    }

    fun getThreadsBaseData(
        client: Gmail,
        username: String,
        search: String? = null,
        labelsNames: List<String> = listOf(),
        pageToken: String? = null
    ): EmailsContainerDTO {
        val labelIds = labelService.getLabelIdsByName(labelsNames, username)

        val (nextToken, ids) = getEmailThreadIdList(client, search, pageToken, labelIds)
        val emailThreads = getThreadsBaseData(client, ids, username)
        return buildEmailContainerDto(pageToken.isNullOrBlank(), nextToken, emailThreads)
    }

    fun getFullEmailThreadData(client: Gmail, username: String, id: String): EmailThreadDto? {
        val emails = getFullEmailThreadData(client, listOf(id), username)
        return emails.firstOrNull()
    }

    fun getFullEmailThreadData(client: Gmail, gmailIds: List<String>, username: String): List<EmailThreadDto> {
        LOGGER.info("Fetching full emails for user: $username with ids $gmailIds")
        val threads = getMutableThreadsList(gmailIds, EmailFormat.FULL, client)

        threads.forEach {
            labelService.updateLabels(username, client, it.id, listOf(), listOf("Unread"))
        }

        return threads.mapNotNull { buildEmailThreadDto(it, username) }
    }

    fun getThreadsBaseData(client: Gmail, gmailIds: List<String>, username: String): List<EmailThread> {
        LOGGER.info("Fetching Metadata emails threads for user: $username with ids $gmailIds")

        val cachedEmailThreads = emailThreadRepository.findAllByUserIdAndEmailsGmailIdIn(username, gmailIds)

        val emailIdsToFetch = mutableListOf<String>()
        emailIdsToFetch.addAll(gmailIds)

        val alreadyCachedEmails = cachedEmailThreads.flatMap { thread ->
            thread.emails.map { email -> email.gmailId }
        }
        alreadyCachedEmails.forEach { emailIdsToFetch.remove(it) }

        val newThreads = getMutableThreadsList(emailIdsToFetch, EmailFormat.METADATA, client)

        val newEmailThreads = saveNewThreadsFromGmail(newThreads, username)

        return cachedEmailThreads + newEmailThreads
    }

    fun hasNewMessageInInbox(
        client: Gmail,
        username: String
    ): Boolean {
        val newMessageResponse = client
            .users()
            .messages()
            .list(USER_SELF_ACCESS)
            .setMaxResults(1)
            .execute()
        val latestMessage = newMessageResponse.messages.firstOrNull()

        if (latestMessage != null) {
            val cachedCount = userEmailRepository.countByGmailIdAndThreadUserId(latestMessage.id, username)
            return cachedCount == 0
        }

        return false
    }

    fun getEmailBaseData(
        client: Gmail,
        gmailIds: List<String>,
        username: String,
        isReply: Boolean = false
    ): List<UserEmail> {
        LOGGER.info("Fetching Metadata emails for user: $username with ids $gmailIds")

        val cachedEmails = userEmailRepository.findAllByThreadUserIdAndGmailIdIn(username, gmailIds)

        val emailIdsToFetch = mutableListOf<String>()
        emailIdsToFetch.addAll(gmailIds)

        val alreadyCachedEmails = cachedEmails.map { it.gmailId }
        alreadyCachedEmails.forEach { emailIdsToFetch.remove(it) }

        val fetchedEmails = getMutableEmailsList(emailIdsToFetch, EmailFormat.METADATA, client)

        val savedEmails = saveNewEmailsFromGmail(fetchedEmails, username, isReply)

        return cachedEmails + savedEmails
    }

    fun getEmailThreadBaseData(client: Gmail, gmailId: String, username: String): EmailThread? {
        return getThreadsBaseData(client, listOf(gmailId), username).firstOrNull()
    }

    fun getEmailBaseData(client: Gmail, gmailId: String, username: String, isReply: Boolean = false): UserEmail? {
        return getEmailBaseData(client, listOf(gmailId), username, isReply).firstOrNull()
    }

    private fun getMutableEmailsList(
        ids: List<String>,
        format: EmailFormat,
        client: Gmail
    ): MutableList<Message> {
        if (ids.isEmpty()) {
            LOGGER.warn("Empty ids unable to fetch data")
            return mutableListOf()
        }

        val messageList = mutableListOf<Message>()
        val batchRequest = client.batch()
        val callback = MessageBatchCallback(messageList)

        ids.forEach {
            client.users()
                .messages()
                .get(USER_SELF_ACCESS, it)
                .setFormat(format.value)
                .queue(batchRequest, callback)
        }

        batchRequest.execute()

        return messageList
    }

    private fun getMutableThreadsList(
        ids: List<String>,
        format: EmailFormat,
        client: Gmail
    ): MutableList<com.google.api.services.gmail.model.Thread> {
        if (ids.isEmpty()) {
            LOGGER.warn("Empty ids unable to fetch data")
            return mutableListOf()
        }
        val threadList = mutableListOf<com.google.api.services.gmail.model.Thread>()
        val batchRequest = client.batch()
        val callback = ThreadBatchCallback(threadList)

        ids.forEach {
            client.users()
                .threads()
                .get(USER_SELF_ACCESS, it)
                .setFormat(format.value)
                .queue(batchRequest, callback)
        }

        batchRequest.execute()

        return threadList
    }

    @Transactional
    fun saveNewThreadsFromGmail(
        threads: List<com.google.api.services.gmail.model.Thread>,
        username: String
    ): List<EmailThread> {
        if (threads.isEmpty()) {
            LOGGER.info("No threads to save for user $username. Email thread cache up to date :)")
            return listOf()
        }

        val user = userService.getUserById(username)

        if (user == null) {
            LOGGER.error("No user found to save messages for.")
            return listOf()
        }

        val threadsToSave = threads.map {
            EmailThread(
                EmailThreadId(it.id, user.id),
                getEmailHeader(it.messages.first(), EmailHeaderName.SUBJECT),
                user
            )
        }

        val savedThreads = emailThreadRepository.saveAll(threadsToSave)
        val savedEmails = saveNewEmailsFromGmail(threads.flatMap { it.messages }, username)
            .groupBy { it.thread?.id?.threadId }

        threads.forEach { thread ->
            val savedThread = savedThreads.firstOrNull { it?.id?.threadId == thread.id }
            savedEmails[thread.id]?.let {
                savedThread?.addAllEmails(it)
            }
        }

        return savedThreads
    }

    fun saveNewEmailsFromGmail(
        emails: List<Message>,
        username: String,
        isReply: Boolean = false
    ): List<UserEmail> {
        if (emails.isEmpty()) {
            LOGGER.info("No messages to save for user $username. Email cache up to date :)")
            return listOf()
        }

        val user = userService.getUserById(username)

        if (user == null) {
            LOGGER.error("No user found to save messages for.")
            return listOf()
        }
        val threadIds = emails.map { it.threadId }
        val cachedThreads = emailThreadRepository.findAllByUserIdAndIdThreadIdIn(username, threadIds)
        val allUserLabels = labelService.getAllUserLabels(username).associateBy { it.id.gmailId }

        val emailsToSave = emails.map { message ->
            val savedThread = cachedThreads.firstOrNull { it.id.threadId == message.threadId }
            val lastSavedMessage = savedThread?.emails?.lastOrNull()
            val email = UserEmail(
                null,
                message.id,
                getEmailHeader(message, EmailHeaderName.MESSAGE_ID),
                getEmailHeader(message, EmailHeaderName.FROM),
                getEmailHeader(message, EmailHeaderName.EMAIL_TO),
                extractEmailFromHeaderString(getEmailHeader(message, EmailHeaderName.CC)),
                LocalDateTime.ofInstant(Instant.ofEpochMilli(message.internalDate), ZoneId.systemDefault()),
                savedThread
            )
            val labels = message.labelIds.mapNotNull { allUserLabels[it] }
            email.addAllLabels(labels)
            email.setCategoryXp(isReply, lastSavedMessage)
            email
        }

        return userEmailRepository.saveAll(emailsToSave)
    }

    private fun extractEmailFromHeaderString(emailString: String): String? {
        if (emailString.isBlank()) {
            return null
        }

        return emailString.split(",").map {
            if (it.contains("<")) {
                it.substringAfter("<").substringBefore(">")
            } else {
                it
            }
        }.joinToString(",")
    }

    fun getEmailHeader(message: Message, headerName: EmailHeaderName): String {
        return message.payload
            .headers
            .firstOrNull { it.name.lowercase() == headerName.value.lowercase() }
            ?.value ?: ""
    }

    fun send(username: String, client: Gmail, sendRequest: SendRequest): EmailThreadDto? {
        return try {
            val message = buildMessageFromSendRequest(sendRequest, username)
                ?: return null

            val sentMessage = client.users()
                .messages()
                .send(USER_SELF_ACCESS, message)
                .execute()

            val thread = getEmailThreadBaseData(client, sentMessage.threadId, username)

            if (thread == null) {
                LOGGER.error("Error fetching new thread info")
                return null
            }
            // todo investigate if it can be included in send request
            labelService.updateLabels(username, client, thread.id.threadId, sendRequest.labels)
            buildEmailThreadDto(thread)
        } catch (e: GoogleJsonResponseException) {
            LOGGER.error("Unable to send message: " + e.details)
            null
        }
    }

    fun sendReply(username: String, client: Gmail, replyRequest: ReplyRequest): EmailDto? {
        val lastEmailInThread = userEmailRepository
            .findAllByThreadIdThreadIdInAndThreadUserIdOrderByDateSent(
                listOf(replyRequest.threadId),
                username
            ).lastOrNull()

        val message = buildMessageFromReplyRequest(username, client, replyRequest)
            ?: return null
        message.threadId = replyRequest.threadId

        val sentMessage = client.users()
            .messages()
            .send(USER_SELF_ACCESS, message)
            .execute()

        // todo update correct message with the reply. Last non user message sent
        lastEmailInThread?.let { userStatsService.updateUserStats(username, StatCategory.REPLY, listOf(it)) }

        val emailDto = buildEmailDtoFromMessage(username, client, sentMessage, isReply = true) ?: return null
        emailDto.body = replyRequest.reply

        return emailDto
    }

    private fun buildEmailDtoFromMessage(
        username: String,
        client: Gmail,
        sentMessage: Message,
        isReply: Boolean = false
    ): EmailDto? {
        val sentEmailSummary = getEmailBaseData(client, sentMessage.id, username, isReply) ?: return null
        return buildEmailDto(sentEmailSummary)
    }

    private fun buildMessageFromSendRequest(sendRequest: SendRequest, username: String): Message? {
        return buildMessage(
            username,
            sendRequest.to,
            sendRequest.cc,
            sendRequest.subject,
            sendRequest.body
        )
    }

    private fun buildMessageFromReplyRequest(
        username: String,
        client: Gmail,
        replyRequest: ReplyRequest
    ): Message? {
        val replyThread = getEmailThreadBaseData(client, replyRequest.threadId, username)
            ?: return null

        val to = replyThread.getLastFromEmail()
        val subject = replyThread.subject
        val replyHeaders = mutableMapOf<String, String>()

        replyHeaders[EmailHeaderName.REFERENCE.value] = replyThread.getAllEmailMimeIds()
        replyHeaders[EmailHeaderName.IN_REPLY_TO.value] = replyThread.getLastSentEmailMimeId()

        val cc = replyThread.emails.mapNotNull { it.cc }
            .flatMap { it.lowercase().split(",") }
            .toSet()
            .toList()

        return buildMessage(
            username,
            listOf(to),
            cc,
            subject,
            replyRequest.reply,
            replyHeaders
        )
    }

    fun buildMimeMessage(
        from: String,
        to: List<String>,
        cc: List<String>,
        subject: String,
        body: String,
        additionalHeaders: Map<String, String> = mapOf()
    ): MimeMessage? {
        if (hasInvalidEmailAddresses(to + from + cc)) {
            LOGGER.info("Stopping mime message build. invalid email addresses")
            return null
        }
        val props = Properties()
        val session = Session.getDefaultInstance(props, null)

        val mimeMessage = MimeMessage(session)
        val mimeMessageHelper = MimeMessageHelper(mimeMessage)

        mimeMessageHelper.setFrom(from)
        mimeMessageHelper.setTo(to.toTypedArray())
        mimeMessageHelper.setCc(cc.toTypedArray())
        mimeMessageHelper.setSubject(subject)
        mimeMessageHelper.setText(body)

        additionalHeaders.entries.forEach {
            mimeMessage.addHeader(it.key, it.value)
        }

        return mimeMessage
    }

    private fun hasInvalidEmailAddresses(to: List<String>): Boolean {
        to.forEach {
            if (!isValidEmailAddress(it)) {
                return true
            }
        }

        return false
    }

    fun buildMessage(
        from: String,
        to: List<String>,
        cc: List<String>,
        subject: String,
        body: String,
        additionalHeaders: Map<String, String> = mapOf()
    ): Message? {
        val mimeMessage = buildMimeMessage(from, to, cc, subject, body, additionalHeaders)
            ?: return null
        val base64String = conversionService.convertMimeMessageToBase64String(mimeMessage)
            ?: return null

        val message = Message()
        message.raw = base64String
        return message
    }

    fun isValidEmailAddress(emailAddress: String): Boolean {
        return try {
            InternetAddress(emailAddress).validate()
            true
        } catch (exception: AddressException) {
            LOGGER.info("invalid email address $emailAddress")
            false
        }
    }

    fun buildEmailContainerDto(
        usedPageToken: Boolean,
        nextPageToken: String?,
        emailThreads: List<EmailThread>
    ): EmailsContainerDTO {
        val emailThreadDto = emailThreads.map { buildEmailThreadDto(it) }

        return EmailsContainerDTO(
            emailThreadDto.sortedByDescending { it.latestDate },
            nextPageToken,
            usedPageToken
        )
    }

    fun buildEmailThreadDto(emailThread: EmailThread): EmailThreadDto {
        return EmailThreadDto(
            emailThread.id.threadId,
            emailThread.subject,
            emailThread.getLastFromEmail(),
            emailThread.emails.maxOfOrNull { it.dateSent },
            emailThread.getFullLabelList(),
            emailThread.emails.map { buildEmailDto(it) }
        )
    }

    fun buildEmailThreadDto(
        emailThread: com.google.api.services.gmail.model.Thread,
        username: String
    ): EmailThreadDto? {
        val cachedEmailThread = emailThreadRepository.findFirstById(
            EmailThreadId(emailThread.id, username)
        ) ?: return null

        return EmailThreadDto(
            emailThread.id,
            cachedEmailThread.subject,
            cachedEmailThread.getLastFromEmail(),
            cachedEmailThread.emails.maxOfOrNull { it.dateSent },
            cachedEmailThread.getFullLabelList(),
            emailThread.messages.map { buildEmailDto(it) }
        )
    }

    fun buildEmailDto(message: Message): EmailDto {
        return EmailDto(
            message.id,
            message.threadId,
            getEmailHeader(message, EmailHeaderName.FROM),
            getEmailHeader(message, EmailHeaderName.EMAIL_TO).split(","),
            extractEmailFromHeaderString(
                getEmailHeader(message, EmailHeaderName.CC)
            )?.split(",") ?: listOf(),
            message.labelIds.contains("SENT"),
            LocalDateTime.ofEpochSecond(message.internalDate, 0, ZoneOffset.UTC),
            getFullBodyFromMessage(message),
            getAttachmentNames(message)
        )
    }

    fun buildEmailDto(userEmail: UserEmail): EmailDto {
        return EmailDto(
            userEmail.gmailId,
            userEmail.thread?.id?.threadId ?: userEmail.gmailId,
            userEmail.fromEmail,
            userEmail.toEmail.split(","),
            userEmail.cc?.split(",") ?: listOf(),
            userEmail.getLabelList().contains("SENT"),
            userEmail.dateSent
        )
    }

    private fun getFullBodyFromMessage(message: Message): String? {
        if (message.payload?.parts == null || message.payload.parts.isEmpty()) {
            return conversionService.getDefaultStyledDiv(message.snippet)
        }
        var fullBody = ""

        message.payload.parts.forEach {
            fullBody += conversionService.convertMessagePartToString(it)
        }

        return if (fullBody.isBlank()) {
            conversionService.getDefaultStyledDiv(message.snippet)
        } else {
            conversionService.getHtmlBody(fullBody)
        }
    }

    fun getAttachmentNames(message: Message): List<AttachmentDTO> {
        return message.payload.parts
            .filter { !it.filename.isNullOrBlank() }
            .map { AttachmentDTO(it.body.attachmentId, it.filename) }
    }

    fun getAttachment(
        client: Gmail,
        username: String,
        attachmentId: String,
        emailId: String
    ): ByteArray? {
        val attachmentBody = client.users()
            .messages()
            .attachments()
            .get(USER_SELF_ACCESS, emailId, attachmentId)
            .execute()
        return conversionService.convertBase64ToByteArray(attachmentBody.data)
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(GmailService::class.java)
        const val USER_SELF_ACCESS = "me"
    }
}
