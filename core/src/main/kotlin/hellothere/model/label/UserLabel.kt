package hellothere.model.label

import hellothere.model.user.User
import javax.persistence.*

@Entity
@Table(name = "user_labels")
class UserLabel(
    @EmbeddedId
    val id: UserLabelId,

    @Column(name = "name")
    val name: String,

    @Column(name = "unread_threads")
    val unreadThreads: Int,

    @ManyToOne
    @MapsId("app_user")
    @JoinColumn(name = "app_user")
    val user: User? = null
)
