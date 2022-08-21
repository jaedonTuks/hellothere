package hellothere.model.email.ids

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.OrderBy

@Embeddable
data class EmailId(
    @Column(name = "gmail_id")
    val gmailId: String,

    @Column(name = "app_user")
    val appUser: String
) : Serializable
