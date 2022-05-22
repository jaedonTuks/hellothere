package hellothere.service.gmail

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.model.Profile
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class GmailService(
    @Value("\${gmail.client.projectId}") private val projectId: String,
    @Value("\${gmail.client.clientId}") private val clientId: String,
    @Value("\${gmail.client.clientSecret}") private val clientSecret: String,
    @Value("\${gmail.client.redirectUrl}") private val redirectUrl: String,
) {
    private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    private val jsonFactory = GsonFactory()
    private lateinit var googleAuthCodeFlow: GoogleAuthorizationCodeFlow

    init {
        val web = GoogleClientSecrets.Details()
        web.clientId = clientId
        web.clientSecret = clientSecret

        val clientSecret = GoogleClientSecrets().setWeb(web)

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

    fun createAndStoreCredential(code: String): Credential {
        // todo update the auth flow store credential to retrieve if userId. This flow will become more complex as you continue
        val response: TokenResponse = googleAuthCodeFlow.newTokenRequest(code).setRedirectUri(redirectUrl).execute()
        return googleAuthCodeFlow.createAndStoreCredential(response, "test")
    }

    fun getGmailClientFromCredentials(credentials: Credential): Gmail {
        return Gmail.Builder(
            httpTransport,
            jsonFactory,
            credentials
        ).setApplicationName(projectId)
            .build()
    }

    fun getGmailClientFromCode(code: String): Gmail {
        val credentials = createAndStoreCredential(code)
        return getGmailClientFromCredentials(credentials)
    }

    fun getEmailIdList(client: Gmail): List<String> {
        LOGGER.info("Fetching emails")
        val messageList = client
            .users()
            .messages()
            .list(USER_SELF_ACCESS)
            .setMaxResults(5)
            .execute()
        // todo explore client.batch()
        return messageList.messages.map { it.id }
    }

    fun getGmailUserInfo(client: Gmail): Profile {
        LOGGER.info("Fetching user info for client $client")

        return client
            .users()
            .getProfile(USER_SELF_ACCESS)
            .execute()
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(GmailService::class.java)
        const val USER_SELF_ACCESS = "me"
    }
}
