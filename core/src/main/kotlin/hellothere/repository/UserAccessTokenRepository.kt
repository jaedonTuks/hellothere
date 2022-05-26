package hellothere.repository

import hellothere.model.user.UserAccessToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface UserAccessTokenRepository : JpaRepository<UserAccessToken, String> {
    fun findFirstByUserIdAndExpiryDateTimeAfter(userId: String, expiryDateTime: LocalDateTime): UserAccessToken?
}
