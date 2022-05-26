package hellothere.service.gmail

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.MemoryDataStoreFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.model.Message
import com.google.api.services.gmail.model.Profile
import hellothere.dto.email.EmailDto
import hellothere.model.email.EmailFormat
import hellothere.model.email.EmailHeaderName
import hellothere.model.user.UserAccessToken
import hellothere.repository.UserAccessTokenRepository
import liquibase.pro.packaged.it
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

@Service
class GmailService(
    @Value("\${gmail.client.projectId}") private val projectId: String,
    @Value("\${gmail.client.clientId}") private val clientId: String,
    @Value("\${gmail.client.clientSecret}") private val clientSecret: String,
    @Value("\${gmail.client.redirectUrl}") private val redirectUrl: String,
    private val userAccessTokenRepository: UserAccessTokenRepository
) {
    private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    private val jsonFactory = GsonFactory()
    private lateinit var googleAuthCodeFlow: GoogleAuthorizationCodeFlow

    init {
        val web = GoogleClientSecrets.Details()
        web.clientId = clientId
        web.clientSecret = clientSecret

        val clientSecret = GoogleClientSecrets().setWeb(web)

        MemoryDataStoreFactory.getDefaultInstance()
// todo handle refresh tokens and credential store
        googleAuthCodeFlow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport,
            jsonFactory,
            clientSecret,
            listOf(GmailScopes.GMAIL_READONLY)
        ).build()
    }

    fun authorize(): String? {
        val authUrl = googleAuthCodeFlow.newAuthorizationUrl()
        authUrl.redirectUri = redirectUrl
        return authUrl.build()
    }

    fun createAndStoreCredential(code: String): Pair<Credential, UserAccessToken> {
        val accessToken = userAccessTokenRepository.findByIdOrNull(code)

        if (accessToken == null || accessToken.hasExpired()) {
            val token: TokenResponse = googleAuthCodeFlow.newTokenRequest(code).setRedirectUri(redirectUrl).execute()

            val credential = googleAuthCodeFlow.createAndStoreCredential(token, "test")

            val newAccessToken = UserAccessToken(
                code,
                token.accessToken,
                token.refreshToken,
                token.scope,
                LocalDateTime.now().plusSeconds(token.expiresInSeconds),
                null
            )

            return Pair(credential, newAccessToken)
        }

        val token = TokenResponse()
            .setAccessToken(accessToken.token)
            .setRefreshToken(accessToken.refreshToken)
            .setScope(accessToken.scope)
            .setExpiresInSeconds(ChronoUnit.SECONDS.between(LocalDateTime.now(), accessToken.expiryDateTime))
        val credential = googleAuthCodeFlow.createAndStoreCredential(token, "test")
        return Pair(credential, accessToken)
    }

    fun getUserCredentialFromUsername(username: String): Credential? {
        return getTokenResponseFromAccessToken(username)?.let {
            googleAuthCodeFlow.createAndStoreCredential(it, username)
        }
    }

    fun getGmailClientFromCredentials(credentials: Credential): Gmail {
        return Gmail.Builder(
            httpTransport,
            jsonFactory,
            credentials
        ).setApplicationName(projectId)
            .build()
    }

    fun getGmailClientFromUsername(username: String): Gmail? {
        return getUserCredentialFromUsername(username)?.let {
            getGmailClientFromCredentials(it)
        }
    }

    fun getTokenResponseFromAccessToken(username: String): TokenResponse? {
        return userAccessTokenRepository.findFirstByUserIdAndExpiryDateTimeAfter(username, LocalDateTime.now())
            ?.let { accessToken ->
                TokenResponse()
                    .setAccessToken(accessToken.token)
                    .setRefreshToken(accessToken.refreshToken)
                    .setScope(accessToken.scope)
                    .setExpiresInSeconds(ChronoUnit.SECONDS.between(LocalDateTime.now(), accessToken.expiryDateTime))
            }
    }

    fun getGmailClientFromCode(code: String): Pair<Gmail, UserAccessToken> {
        val (credentials, userAccessToken) = createAndStoreCredential(code)

        return Pair(getGmailClientFromCredentials(credentials), userAccessToken)
    }

    fun getEmailIdList(client: Gmail): List<String> {
        LOGGER.info("Fetching email ids")

        val messageList = client
            .users()
            .messages()
            .list(USER_SELF_ACCESS)
            .setMaxResults(5)
            .execute()

        return messageList.messages.map { it.id }
    }

    fun getEmailsBaseData(client: Gmail): List<EmailDto> {
        LOGGER.info("Fetching full emails")
        val ids = getEmailIdList(client)
        val emails = getEmailsBaseData(client, ids)
        return emails.map { buildEmailDto(it) }
    }

    fun getEmailsBaseData(client: Gmail, ids: List<String>): List<Message> {
        LOGGER.info("Fetching full emails")

        // todo explore client.batch() in beta version
        // will require you to build your own http request

        val emails = ids.firstOrNull()?.let {
            client
                .users()
                .messages()
                .get(USER_SELF_ACCESS, it)
                .setFormat(EmailFormat.METADATA.value)
                .execute()
        }

        return emails?.let { listOf(it) } ?: listOf()
    }

    fun getGmailUserInfo(client: Gmail): Profile {
        LOGGER.info("Fetching user info for client $client")

        return client
            .users()
            .getProfile(USER_SELF_ACCESS)
            .execute()
    }

    fun getEmailHeader(message: Message, headerName: EmailHeaderName): String {
        return message.payload
            .headers
            .firstOrNull { it.name == headerName.value }
            ?.value ?: ""
    }

    fun buildEmailDto(message: Message): EmailDto {
        return EmailDto(
            message.id,
            getEmailHeader(message, EmailHeaderName.FROM),
            LocalDateTime.ofEpochSecond(message.internalDate, 0, ZoneOffset.UTC),
            message.labelIds,
            getEmailHeader(message, EmailHeaderName.SUBJECT),
            message.snippet
        )
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(GmailService::class.java)
        const val USER_SELF_ACCESS = "me"
    }
}
