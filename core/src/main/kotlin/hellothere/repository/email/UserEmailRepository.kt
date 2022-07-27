package hellothere.repository.email

import hellothere.model.email.UserEmail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserEmailRepository : JpaRepository<UserEmail, String> {
    fun findAllByThreadUserId(userId: String): List<UserEmail>

    fun findAllByThreadThreadIdAndThreadUserId(threadId: String, userId: String): List<UserEmail>

    fun findAllByThreadUserIdAndGmailIdIn(userId: String, gmailId: List<String>): List<UserEmail>
}
