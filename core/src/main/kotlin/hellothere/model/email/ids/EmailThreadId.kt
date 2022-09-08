package hellothere.model.email.ids

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.OrderBy

@Embeddable
data class EmailThreadId(
    @Column(name = "thread_id")
    val threadId: String,

    @Column(name = "app_user")
    val appUser: String
) : Serializable
