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

    @Column(name = "color")
    val color: String = "#FFF",

    @Column(name = "unread_threads")
    val unreadThreads: Int,

    @Column(name = "is_manageable")
    val isManageable: Boolean,

    @ManyToOne
    @MapsId("app_user")
    @JoinColumn(name = "app_user")
    val user: User? = null,
) {
    companion object {
        private val nonManageableIds = listOf(
            "INBOX",
            "UNREAD",
            "SPAM",
            "TRASH",
            "SENT",
            "DRAFT"
        )

        fun isManageableId(id: String): Boolean {
            return !nonManageableIds.contains(id)
        }
    }
}
