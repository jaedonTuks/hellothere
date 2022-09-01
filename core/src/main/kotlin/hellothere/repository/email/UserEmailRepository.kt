package hellothere.repository.email

import hellothere.model.email.UserEmail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserEmailRepository : JpaRepository<UserEmail, Long> {
    @Query("select count(distinct ue.thread.id.threadId) from UserEmail ue where ue.thread.id.appUser = :username")
    fun countDistinctByThreadUserId(username: String): Int
    fun countByThreadUserId(username: String): Int

    fun countByGmailIdAndThreadUserId(gmailId: String, threadUserId: String): Int

    fun countByThreadUserIdAndLabelXPAllocatedDateNotNull(username: String): Int
    fun countByThreadUserIdAndReadXpAllocatedDateNotNull(username: String): Int
    fun countByThreadUserIdAndReplyXPAllocatedDateNotNull(username: String): Int

    fun findAllByThreadIdThreadIdInAndThreadUserIdOrderByDateSent(threadIds: List<String>, userId: String): List<UserEmail>

    fun findAllByThreadUserIdAndGmailIdIn(userId: String, gmailId: List<String>): List<UserEmail>
}
