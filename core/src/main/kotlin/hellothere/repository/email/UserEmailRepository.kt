package hellothere.repository.email

import hellothere.model.email.UserEmail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserEmailRepository : JpaRepository<UserEmail, String> {
    fun findAllByUserId(userId: String): List<UserEmail>

    fun findAllByUserIdAndGmailIdIn(userId: String, gmailId: List<String>): List<UserEmail>
}
