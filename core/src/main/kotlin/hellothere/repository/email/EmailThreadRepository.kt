package hellothere.repository.email

import hellothere.model.email.EmailThread
import hellothere.model.email.ids.EmailThreadId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailThreadRepository : JpaRepository<EmailThread, EmailThreadId> {
    fun findAllByUserIdAndIdThreadIdIn(userId: String, threadId: List<String>): List<EmailThread>

    fun findAllByUserIdAndEmailsGmailIdIn(userId: String, emailsGmailId: List<String>): List<EmailThread>

    fun findFirstById(threadId: EmailThreadId): EmailThread?
}
