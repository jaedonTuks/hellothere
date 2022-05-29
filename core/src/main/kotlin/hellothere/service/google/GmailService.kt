package hellothere.service.google

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Message
import hellothere.dto.email.EmailDto
import hellothere.model.email.EmailFormat
import hellothere.model.email.EmailHeaderName
import hellothere.model.email.UserEmail
import hellothere.model.user.UserAccessToken
import hellothere.repository.email.UserEmailRepository
import hellothere.requests.email.ReplyRequest
import hellothere.requests.email.SendRequest
import hellothere.service.ConversionService
import hellothere.service.user.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
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
    private val userEmailRepository: UserEmailRepository,
    private val googleAuthenticationService: GoogleAuthenticationService,
    private val userService: UserService,
    private val conversionService: ConversionService,
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

    fun getEmailIdList(
        client: Gmail,
        query: String? = null,
        labelIds: List<String> = listOf()
    ): List<String> {
        LOGGER.info("Fetching email ids with search query: {$query} and ids {$labelIds}")

        val messageList = client
            .users()
            .messages()
            .list(USER_SELF_ACCESS)
            .setMaxResults(5)

        if (query != null) {
            messageList.q = query
        }

        if (labelIds.isNotEmpty()) {
            messageList.labelIds = labelIds
        }

        return messageList
            .execute()
            .messages.map { it.id }
    }

    fun getEmailsBaseData(
        client: Gmail,
        username: String,
        search: String? = null,
        labels: List<String> = listOf()
    ): List<EmailDto> {
        val ids = getEmailIdList(client, search, labels)
        val emails = getEmailsBaseData(client, ids, username)
        return emails.map { buildEmailDto(it) }
    }

    fun getFullEmailData(client: Gmail, username: String, id: String): EmailDto? {
        val emails = getFullEmailData(client, listOf(id), username)
        return emails.firstOrNull()
    }

    fun getFullEmailData(client: Gmail, gmailIds: List<String>, username: String): List<EmailDto> {
        LOGGER.info("Fetching full emails for user: $username with ids $gmailIds")
        // todo update to fetch thread not just email
        val messages = getMutableMessagesList(gmailIds, EmailFormat.FULL, client)
        return messages.map { buildEmailDto(it) }
    }

    fun getEmailsBaseData(client: Gmail, gmailIds: List<String>, username: String): List<UserEmail> {
        LOGGER.info("Fetching Metadata emails for user: $username with ids $gmailIds")

        val cachedEmails = userEmailRepository.findAllByUserIdAndGmailIdIn(username, gmailIds)

        val emailIdsToFetch = mutableListOf<String>()
        emailIdsToFetch.addAll(gmailIds)
        cachedEmails.forEach { emailIdsToFetch.remove(it.gmailId) }

        val newMessages = getMutableMessagesList(emailIdsToFetch, EmailFormat.METADATA, client)

        val newEmails = saveNewEmailsFromGmail(newMessages, username)

        return newEmails + cachedEmails
    }

    fun getEmailBaseData(client: Gmail, gmailId: String, username: String): UserEmail? {
        return getEmailsBaseData(client, listOf(gmailId), username).firstOrNull()
    }

    private fun getMutableMessagesList(ids: List<String>, format: EmailFormat, client: Gmail): MutableList<Message> {
        // todo explore client.batch() in beta version
        // will require you to build your own http request
        return ids.map {
            client
                .users()
                .messages()
                .get(USER_SELF_ACCESS, it)
                .setFormat(format.value)
                .execute()
        }.toMutableList()
    }

    fun saveNewEmailsFromGmail(messages: List<Message>, username: String): List<UserEmail> {
        if (messages.isEmpty()) {
            LOGGER.info("No messages to save for user $username. Email cache up to date :)")
            return listOf()
        }

        val user = userService.getUserById(username)

        if (user == null) {
            LOGGER.error("No user found to save messages for.")
            return listOf()
        }

        val emailsToSave = messages.map {
            UserEmail(
                null,
                it.id,
                it.threadId,
                getEmailHeader(it, EmailHeaderName.MESSAGE_ID),
                getEmailHeader(it, EmailHeaderName.SUBJECT),
                getEmailHeader(it, EmailHeaderName.FROM),
                LocalDateTime.ofInstant(Instant.ofEpochMilli(it.internalDate), ZoneId.systemDefault()),
                it.labelIds.joinToString(","),
                user
            )
        }

        return userEmailRepository.saveAll(emailsToSave)
    }

    fun getEmailHeader(message: Message, headerName: EmailHeaderName): String {
        return message.payload
            .headers
            .firstOrNull { it.name.lowercase() == headerName.value.lowercase() }
            ?.value ?: ""
    }

    fun send(username: String, client: Gmail, sendRequest: SendRequest): Message? {
        return try {
            val message = buildMessageFromSendRequest(sendRequest, username)
                ?: return null
            val sentMessage = client.users().messages().send(USER_SELF_ACCESS, message).execute()
            sentMessage
        } catch (e: GoogleJsonResponseException) {
            LOGGER.error("Unable to send message: " + e.details)
            null
        }
    }

    fun sendReply(username: String, client: Gmail, replyRequest: ReplyRequest): Message? {
        val message = buildMessageFromReplyRequest(username, client, replyRequest)
            ?: return null
        message.threadId = replyRequest.threadId

        val sentMessage = client.users()
            .messages()
            .send(USER_SELF_ACCESS, message)
            .execute()

        return sentMessage
    }

    private fun buildMessageFromSendRequest(sendRequest: SendRequest, username: String): Message? {
        return buildMessage(
            username,
            sendRequest.to,
            sendRequest.subject,
            sendRequest.body
        )
    }

    private fun buildMessageFromReplyRequest(
        username: String,
        client: Gmail,
        replyRequest: ReplyRequest
    ): Message? {
        val emailToReplyTo = getEmailBaseData(client, replyRequest.messageId, username)
            ?: return null

        val to = emailToReplyTo.fromEmail
        val subject = emailToReplyTo.subject
        val replyHeaders = mutableMapOf<String, String>()
        // todo investigate the thread id here

        replyHeaders[EmailHeaderName.REFERENCE.value] = emailToReplyTo.mimeMessageId
        replyHeaders[EmailHeaderName.IN_REPLY_TO.value] = emailToReplyTo.mimeMessageId

        return buildMessage(
            username,
            to,
            subject,
            replyRequest.reply,
            replyHeaders
        )
    }

    fun buildMimeMessage(
        from: String,
        to: String,
        subject: String,
        body: String,
        additionalHeaders: Map<String, String> = mapOf()
    ): MimeMessage? {
        if (!isValidEmailAddress(from) || !isValidEmailAddress(to)) {
            LOGGER.info("Stopping mime message build. invalid email addresses")
            return null
        }
        val props = Properties()
        val session = Session.getDefaultInstance(props, null)

        val mimeMessage = MimeMessage(session)
        val mimeMessageHelper = MimeMessageHelper(mimeMessage)

        mimeMessageHelper.setFrom(from)
        mimeMessageHelper.setTo(to)
        mimeMessageHelper.setSubject(subject)
        mimeMessageHelper.setText(body)

        additionalHeaders.entries.forEach {
            mimeMessage.addHeader(it.key, it.value)
        }

        return mimeMessage
    }

    fun buildMessage(
        from: String,
        to: String,
        subject: String,
        body: String,
        additionalHeaders: Map<String, String> = mapOf()
    ): Message? {
        val mimeMessage = buildMimeMessage(from, to, subject, body, additionalHeaders)
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
            return true
        } catch (exception: AddressException) {
            LOGGER.info("invalid email address $emailAddress")
            return false
        }
    }

    fun buildEmailDto(message: Message): EmailDto {
        return EmailDto(
            message.id,
            message.threadId,
            getEmailHeader(message, EmailHeaderName.FROM),
            LocalDateTime.ofEpochSecond(message.internalDate, 0, ZoneOffset.UTC),
            message.labelIds,
            getEmailHeader(message, EmailHeaderName.SUBJECT),
            getFullBodyFromMessage(message)
        )
    }

    fun buildEmailDto(userEmail: UserEmail): EmailDto {
        return EmailDto(
            userEmail.gmailId,
            userEmail.threadId,
            userEmail.fromEmail,
            userEmail.dateSent,
            userEmail.getLabelList(),
            userEmail.subject,
            null
        )
    }

    private fun getFullBodyFromMessage(message: Message): String? {
        if (message.payload?.parts == null || message.payload.parts.isEmpty()) {
            return message.snippet
        }
        var fullBody = ""

        message.payload.parts.forEach {
            fullBody += conversionService.convertMessagePartToString(it)
        }

        return if (fullBody.isBlank()) {
            message.snippet
        } else {
            conversionService.getHtmlBody(fullBody)
        }
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(GmailService::class.java)
        const val USER_SELF_ACCESS = "me"
    }
}
