package hellothere.repository.email

import hellothere.model.email.EmailThread
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailThreadRepository : JpaRepository<EmailThread, String> {
    fun findAllByUserIdAndThreadIdIn(userId: String, threadId: List<String>): List<EmailThread>

    fun findAllByUserIdAndEmailsGmailIdIn(userId: String, emailsGmailId: List<String>): List<EmailThread>

    fun findFirstByThreadId(threadId: String): EmailThread?
}
