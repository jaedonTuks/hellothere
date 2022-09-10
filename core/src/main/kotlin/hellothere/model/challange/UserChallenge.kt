package hellothere.model.challange

import hellothere.model.user.User
import liquibase.pro.packaged.it
import javax.persistence.*

@Entity
@Table(name = "user_challenge")
class UserChallenge(
    @EmbeddedId
    val id: UserChallengeId
) {

    @ManyToOne
    @MapsId("challenge_id")
    @JoinColumn(name = "challenge_id")
    val challenge: Challenge? = null

    @ManyToOne
    @MapsId("app_user")
    @JoinColumn(name = "app_user")
    val user: User? = null

    @Column(name = "current_progress")
    var currentProgress: Int = 0

    @Column(name = "is_reward_claimed")
    var isRewardClaimed: Boolean = false

    fun isComplete(): Boolean {
        return isRewardClaimed || challenge?.goal?.let {
            currentProgress >= it
        } ?: false
    }
}
