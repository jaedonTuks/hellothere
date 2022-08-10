package hellothere.model.challange

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class UserChallengeId(
    @Column(name = "challenge_id")
    val challengeId: Long,

    @Column(name = "app_user")
    val appUser: String
) : Serializable
