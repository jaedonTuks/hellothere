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
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

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

        // todo handle refresh tokens and credential store
        googleAuthCodeFlow = GoogleAuthorizationCodeFlow.Builder(
            httpTransport,
            jsonFactory,
            clientSecret,
            listOf(GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_SEND)
        ).build()
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
            code,
            token.accessToken,
            token.refreshToken,
            token.scope,
            LocalDateTime.now().plusSeconds(token.expiresInSeconds),
            null
        )

        return Pair(credential, newAccessToken)
    }

    fun getUserCredentialFromUsername(username: String): Credential? {
        return getTokenResponseFromAccessToken(username)?.let {
            googleAuthCodeFlow.createAndStoreCredential(it, username)
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


}
