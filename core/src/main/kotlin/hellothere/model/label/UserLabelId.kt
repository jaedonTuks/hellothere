package hellothere.model.label

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.OrderBy

@Embeddable
data class UserLabelId(
    @Column(name = "gmail_id")
    @OrderBy
    val gmailId: String,

    @Column(name = "app_user")
    val appUser: String
) : Serializable
