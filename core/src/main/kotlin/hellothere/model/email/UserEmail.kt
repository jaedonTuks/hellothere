package hellothere.model.email

import hellothere.model.user.User
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "user_email_summary")
class UserEmail(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "gmail_id")
    val gmailId: String,

    @Column(name = "thread_id")
    val threadId: String,

    @Column(name = "mime_message_id")
    val mimeMessageId: String,

    @Column(name = "subject")
    val subject: String,

    @Column(name = "fromEmail")
    val fromEmail: String,

    @Column(name = "date_sent")
    val dateSent: LocalDateTime,

    @Column(name = "labelIdsString")
    val labelIdsString: String,

    @ManyToOne
    @JoinColumn(name = "app_user")
    var user: User? = null
) {
    fun getLabelList(): List<String> {
        return labelIdsString.split(",")
    }
}
