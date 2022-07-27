package hellothere.model.email

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

    @Column(name = "mime_message_id")
    val mimeMessageId: String,

    @Column(name = "from_email")
    val fromEmail: String,

    @Column(name = "date_sent")
    val dateSent: LocalDateTime,

    @Column(name = "labelIdsString")
    var labelIdsString: String,

    @ManyToOne
    @JoinColumn(name = "thread_id")
    var thread: EmailThread? = null
) {
    fun getLabelList(): List<String> {
        return labelIdsString.split(",")
    }
}
