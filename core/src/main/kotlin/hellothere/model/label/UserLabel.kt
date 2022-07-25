package hellothere.model.label

import hellothere.model.user.User
import javax.persistence.*

@Entity
@Table(name = "user_labels")
class UserLabel(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "gmail_id")
    val gmailId: String,

    @Column(name = "name")
    val name: String,

    @Column(name = "unread_threads")
    val unreadThreads: Int,

    @ManyToOne
    @JoinColumn(name = "app_user")
    val user: User? = null
)
