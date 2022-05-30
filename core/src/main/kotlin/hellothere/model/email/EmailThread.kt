package hellothere.model.email

import hellothere.config.JPA
import hellothere.model.user.User
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import javax.persistence.*

@Entity
@Table(name = "user_thread")
class EmailThread(
    @Id
    @Column(name = "thread_id")
    val threadId: String,

    @Column(name = "subject")
    val subject: String,

    @ManyToOne
    @JoinColumn(name = "app_user")
    var user: User? = null,

    @OneToMany(
        mappedBy = "thread",
        fetch = FetchType.LAZY,
    )
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = JPA.BATCH_SIZE)
    @OrderBy("dateSent DESC")
    val emails: MutableList<UserEmail> = mutableListOf()
) {
    fun addEmail(userEmail: UserEmail) {
        emails.add(userEmail)
    }

    fun addAllEmails(userEmails: List<UserEmail>) {
        emails.addAll(userEmails)
    }

    fun getFullLabelList(): List<String> {
        return emails.flatMap { it.getLabelList() }.toSet().toList()
    }

    fun getLastSentEmailMimeId(): String {
        return emails.lastOrNull()?.mimeMessageId ?: ""
    }

    fun getLastFromEmail(): String {
        return emails.lastOrNull()?.fromEmail ?: ""
    }

    fun getAllEmailMimeIds(): String {
        return emails.joinToString { "${it.mimeMessageId} " }
    }
}
