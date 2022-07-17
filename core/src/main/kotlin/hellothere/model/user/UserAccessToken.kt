package hellothere.model.user

import com.google.api.client.auth.oauth2.TokenResponse
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.persistence.*

@Entity
@Table(name = "user_access_token")
class UserAccessToken(
    @Id
    @Column(name = "id")
    val id: String,

    @Column(name = "token")
    var token: String,

    @Column(name = "refresh_token")
    val refreshToken: String?,

    @Column(name = "scope")
    val scope: String,

    @Column(name = "expires")
    var expiryDateTime: LocalDateTime,

    @ManyToOne
    @JoinColumn(name = "app_user")
    var user: User? = null
) {
    fun hasExpired(): Boolean {
        return expiryDateTime >= LocalDateTime.now()
    }

    fun hasNotExpired(): Boolean {
        return expiryDateTime < LocalDateTime.now()
    }

    fun getTokenResponse(): TokenResponse {
        return TokenResponse()
            .setAccessToken(token)
            .setRefreshToken(refreshToken)
            .setScope(scope)
            .setExpiresInSeconds(ChronoUnit.SECONDS.between(LocalDateTime.now(), expiryDateTime))
    }
}
