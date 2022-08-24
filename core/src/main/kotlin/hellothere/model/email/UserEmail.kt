package hellothere.model.email

import hellothere.model.label.UserLabel
import hellothere.model.stats.category.StatCategory
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "user_email_summary")
class UserEmail(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(name = "gmail_id")
    val gmailId: String,

    @Column(name = "mime_message_id")
    val mimeMessageId: String,

    @Column(name = "from_email")
    val fromEmail: String,

    @Column(name = "email_to")
    val toEmail: String,

    @Column(name = "date_sent")
    val dateSent: LocalDateTime,

    @ManyToOne
    @JoinColumns(
        JoinColumn(name = "thread_id", referencedColumnName = "thread_id"),
        JoinColumn(name = "app_user", referencedColumnName = "app_user")
    )
    var thread: EmailThread? = null
) {
    @Column(name = "has_had_read_xp_allocated")
    var readXpAllocatedDate: LocalDateTime? = null

    @Column(name = "has_had_label_xp_allocated")
    var labelXPAllocatedDate: LocalDateTime? = null

    @Column(name = "has_had_reply_xp_allocated")
    var replyXPAllocatedDate: LocalDateTime? = null

    @OneToMany
    @JoinTable(
        name = "email_labels",
        joinColumns = [JoinColumn(name = "email_id")],
        inverseJoinColumns = [
            JoinColumn(name = "app_user"),
            JoinColumn(name = "label_id")
        ]
    )
    val emailLabels: MutableSet<UserLabel> = mutableSetOf()

    fun addAllLabels(newLabels: List<UserLabel>) {
        emailLabels.addAll(newLabels)
    }
    fun removeAllLabels(oldLabels: List<UserLabel>) {
        emailLabels.removeAll(oldLabels)
    }

    fun getLabelList(): List<String> {
        return emailLabels.map { it.name }
    }

    fun hasHadCategoryXpAllocated(statCategory: StatCategory): Boolean {
        val actionDate = when (statCategory) {
            StatCategory.READ -> readXpAllocatedDate
            StatCategory.LABEL -> labelXPAllocatedDate
            StatCategory.REPLY -> replyXPAllocatedDate
            else -> null
        }
        return actionDate != null
    }

    fun setCategoryXp(isReply: Boolean, lastSavedMessage: UserEmail?) {
        val replyAndReadDate = if (isReply) {
            LocalDateTime.now()
        } else {
            null
        }

        replyXPAllocatedDate = replyAndReadDate
        readXpAllocatedDate = replyAndReadDate
        labelXPAllocatedDate = lastSavedMessage?.labelXPAllocatedDate
    }

    fun markCompletedCategoryXP(category: StatCategory) {
        val now = LocalDateTime.now()
        when (category) {
            StatCategory.READ -> readXpAllocatedDate = now
            StatCategory.LABEL -> labelXPAllocatedDate = now
            StatCategory.REPLY -> replyXPAllocatedDate = now
        }
    }
}
