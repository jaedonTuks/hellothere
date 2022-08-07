package hellothere.repository.email

import hellothere.model.email.UserEmail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserEmailRepository : JpaRepository<UserEmail, String> {
    fun findAllByGmailIdIn(id: List<String>): List<UserEmail>

    fun findAllByThreadThreadIdAndThreadUserId(threadId: String, userId: String): List<UserEmail>

    @Query("select count(distinct ue.thread.threadId) from UserEmail ue where ue.thread.user.id = :username")
    fun countDistinctByThreadUserId(username: String): Int
    fun countByThreadUserId(username: String): Int

    fun countByThreadUserIdAndLabelXPAllocatedDateNotNull(username: String): Int
    fun countByThreadUserIdAndReadXpAllocatedDateNotNull(username: String): Int
    fun countByThreadUserIdAndReplyXPAllocatedDateNotNull(username: String): Int

    fun findAllByThreadThreadIdInAndThreadUserIdOrderByDateSent(threadIds: List<String>, userId: String): List<UserEmail>

    fun findAllByThreadUserIdAndGmailIdIn(userId: String, gmailId: List<String>): List<UserEmail>
}
