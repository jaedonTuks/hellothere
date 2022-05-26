package hellothere.model.user

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "user_access_token")
class UserAccessToken(
    @Id
    @Column(name = "id")
    val id: String,

    @Column(name = "token")
    val token: String,

    @Column(name = "refresh_token")
    val refreshToken: String?,

    @Column(name = "scope")
    val scope: String,

    @Column(name = "expires")
    val expiryDateTime: LocalDateTime,

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
}
