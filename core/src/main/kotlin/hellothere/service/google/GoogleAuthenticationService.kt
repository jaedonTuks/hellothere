package hellothere.service.google

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.GmailScopes
import hellothere.model.user.UserAccessToken
import hellothere.repository.user.UserAccessTokenRepository
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class GoogleAuthenticationService(
    @Value("\${gmail.client.clientId}") private val clientId: String,
    @Value("\${gmail.client.clientSecret}") private val clientSecret: String,
    @Value("\${gmail.client.redirectUrl}") private val redirectUrl: String,
    private val userAccessTokenRepository: UserAccessTokenRepository,
) {
    final val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    final val jsonFactory = GsonFactory()
    private final var googleAuthCodeFlow: GoogleAuthorizationCodeFlow

    init {
        val web = GoogleClientSecrets.Details()
        web.clientId = clientId
        web.clientSecret = clientSecret

        val clientSecret = GoogleClientSecrets().setWeb(web)

        // todo use a credential store?
        googleAuthCodeFlow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport,
            jsonFactory,
            clientSecret,
            listOf(GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_SEND)
        ).setAccessType("offline")
            .build()
    }

    fun authorize(): String? {
        val authUrl = googleAuthCodeFlow.newAuthorizationUrl()
        authUrl.redirectUri = redirectUrl
        return authUrl.build()
    }

    fun createAndStoreCredential(code: String): Pair<Credential, UserAccessToken> {
        val token: TokenResponse = googleAuthCodeFlow.newTokenRequest(code).setRedirectUri(redirectUrl).execute()
        val credential = googleAuthCodeFlow.createAndStoreCredential(token, "test")

        val newAccessToken = UserAccessToken(
            null,
            token.accessToken,
            token.refreshToken,
            token.scope,
            LocalDateTime.now().plusSeconds(token.expiresInSeconds)
        )

        return Pair(credential, newAccessToken)
    }

    fun getUserCredentialFromUsername(username: String): Credential? {
        val tokenResponse = getTokenResponseFromAccessToken(username) ?: return null
        val credential = googleAuthCodeFlow.createAndStoreCredential(tokenResponse, username)

        if (
            credential.accessToken == null ||
            credential.expiresInSeconds != null &&
            credential.expiresInSeconds < 60L

        ) {
            if (!refreshCredential(credential, username)) {
                return null
            }
        } else {
            LOGGER.info("No need to refresh credential for user: $username.")
        }

        return credential
    }

    fun getTokenResponseFromAccessToken(username: String): TokenResponse? {
        val userStoredToken = userAccessTokenRepository.findFirstByIdAndRefreshTokenNotNull(username)
        return userStoredToken?.getTokenResponse()
    }

    fun refreshCredential(credential: Credential, username: String): Boolean {
        try {
            val refreshSuccess = credential.refreshToken()
            val storedCredentials = userAccessTokenRepository.findFirstByIdAndRefreshTokenNotNull(username)
            return if (refreshSuccess && storedCredentials != null) {
                storedCredentials.token = credential.accessToken
                storedCredentials.expiryDateTime = LocalDateTime.now().plusSeconds(credential.expiresInSeconds)
                userAccessTokenRepository.save(storedCredentials)

                LOGGER.info("Refreshed $username credentials")
                true
            } else {
                LOGGER.info("Error refreshing $username credentials.")
                false
            }
        } catch (e: Exception) {
            LOGGER.info("Error refreshing $username credentials.")
            return false
        }
    }

    companion object {
        val LOGGER: Logger = org.slf4j.LoggerFactory.getLogger(GoogleAuthenticationService::class.java)
    }
}
