package hellothere.repository.challenge

import hellothere.model.challange.UserChallenge
import hellothere.model.challange.UserChallengeId
import hellothere.model.stats.category.StatCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserChallengeRepository : JpaRepository<UserChallenge, UserChallengeId> {
    fun findAllByIdAppUserAndChallengeStatCategoryOrderByChallengeId(userId: String, statCategory: StatCategory): List<UserChallenge>

    fun findAllByIdAppUserOrderByChallengeId(userId: String): List<UserChallenge>

    fun findFirstById(userChallengeId: UserChallengeId): UserChallenge?

    fun findAllByIdAppUserAndIsRewardClaimedTrue(userId: String): List<UserChallenge>

    fun countAllByIdAppUserAndIsRewardClaimedTrue(userId: String): Int
}
