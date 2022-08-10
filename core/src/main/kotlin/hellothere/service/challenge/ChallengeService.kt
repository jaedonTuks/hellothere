package hellothere.service.challenge

import hellothere.dto.UserChallengeDTO
import hellothere.model.challange.UserChallenge
import hellothere.model.challange.UserChallengeId
import hellothere.model.stats.category.StatCategory
import hellothere.model.user.User
import hellothere.repository.challenge.ChallengeRepository
import hellothere.repository.challenge.UserChallengeRepository
import hellothere.requests.challenge.ClaimChallengeRewardRequest
import org.slf4j.Logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChallengeService(
    private val userChallengeRepository: UserChallengeRepository,
    private val challengeRepository: ChallengeRepository,
) {
    @Transactional
    fun createUsersDefaultChallenges(user: User) {
        LOGGER.info("Creating challenges for user ${user.id}")

        val allChallenges = challengeRepository.findAll()

        val userChallenges = allChallenges.map {
            UserChallenge(
                UserChallengeId(
                    it.id,
                    user.id
                )
            )
        }

        LOGGER.info("Saving ${userChallenges.size} challenges for user ${user.id}")
        userChallengeRepository.saveAll(userChallenges)
    }

    @Transactional
    fun updateUserChallenges(username: String, statCategory: StatCategory) {
        // todo deactivate this calculation for first week using ff4j feature switch
        val userChallenges = userChallengeRepository.findAllByIdAppUserAndChallengeStatCategory(username, statCategory)
            .filterNot { it.isComplete() }

        userChallenges.forEach { it.currentProgress++ }
        userChallengeRepository.saveAll(userChallenges)
    }

    @Transactional
    fun claimUserChallengeRewardAndReturnXP(claimChallengeRewardRequest: ClaimChallengeRewardRequest): UserChallenge? {
        val userChallenge = userChallengeRepository.findFirstById(
            UserChallengeId(
                claimChallengeRewardRequest.challengeId,
                claimChallengeRewardRequest.username
            )
        )
        if (userChallenge?.challenge == null) {
            LOGGER.error("Unable to find user challenge and mark as complete with username ${claimChallengeRewardRequest.username} and challenge id ${claimChallengeRewardRequest.username}")
            return null
        }
        userChallenge.isRewardClaimed = true
        return userChallengeRepository.save(userChallenge)
    }

    @Transactional
    fun getUserChallengesDTO(username: String): List<UserChallengeDTO> {
        val userChallenges = userChallengeRepository.findAllByIdAppUser(username)
        return userChallenges.mapNotNull {
            buildUserChallengeDTO(it)
        }
    }

    fun buildUserChallengeDTO(userChallenge: UserChallenge): UserChallengeDTO? {
        return userChallenge.challenge?.let { challenge ->
            UserChallengeDTO(
                challenge.id,
                challenge.name,
                challenge.goal,
                userChallenge.currentProgress,
                challenge.reward,
                challenge.description,
                challenge.statCategory,
                userChallenge.isRewardClaimed
            )
        }
    }

    companion object {
        val LOGGER: Logger = org.slf4j.LoggerFactory.getLogger(ChallengeService::class.java)
    }
}
