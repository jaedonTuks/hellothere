package hellothere.model.email

import hellothere.model.label.UserLabel
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

    @ManyToOne
    @JoinColumn(name = "thread_id")
    var thread: EmailThread? = null,
) {
    // todo maybe investigate here https://www.baeldung.com/jpa-many-to-many
    @OneToMany
    @JoinTable(
        name = "email_labels",
        joinColumns = [JoinColumn(name = "email_id")],
        inverseJoinColumns = [JoinColumn(name = "label_id")]
    )
    val emailLabels: MutableSet<UserLabel> = mutableSetOf()

    fun addLabel(newLabel: UserLabel) {
        emailLabels.add(newLabel)
    }

    fun addAllLabels(newLabels: List<UserLabel>) {
        emailLabels.addAll(newLabels)
    }

    fun removeLabel(oldLabel: UserLabel) {
        emailLabels.remove(oldLabel)
    }

    fun removeAllLabels(oldLabels: List<UserLabel>) {
        emailLabels.removeAll(oldLabels)
    }

    fun getLabelList(): List<String> {
        return emailLabels.map { it.gmailId }
    }
}
