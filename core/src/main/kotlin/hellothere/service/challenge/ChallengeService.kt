package hellothere.service.challenge

import hellothere.dto.UserChallengeDTO
import hellothere.model.challange.UserChallenge
import hellothere.model.challange.UserChallengeId
import hellothere.model.feature.FF4jFeature
import hellothere.model.stats.category.StatCategory
import hellothere.model.user.User
import hellothere.repository.challenge.ChallengeRepository
import hellothere.repository.challenge.UserChallengeRepository
import hellothere.requests.challenge.ClaimChallengeRewardRequest
import hellothere.service.FeatureService
import org.slf4j.Logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChallengeService(
    private val userChallengeRepository: UserChallengeRepository,
    private val challengeRepository: ChallengeRepository,
    private val featureService: FeatureService,
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
        if (featureService.isDisabled(FF4jFeature.CHALLENGES)) {
            return
        }
        LOGGER.info("Updating challenge for $username in $statCategory")
        val userChallenges = userChallengeRepository.findAllByIdAppUserAndChallengeStatCategoryOrderByChallengeId(username, statCategory)
            .filterNot { it.isComplete() }

        userChallenges.forEach { it.currentProgress++ }
        userChallengeRepository.saveAll(userChallenges)
    }

    @Transactional
    fun markUserChallengeAsClaimed(claimChallengeRewardRequest: ClaimChallengeRewardRequest): UserChallenge? {
        val userChallenge = userChallengeRepository.findFirstById(
            UserChallengeId(
                claimChallengeRewardRequest.challengeId,
                claimChallengeRewardRequest.username
            )
        )

        if (userChallenge?.challenge == null) {
            LOGGER.error("Unable to find user challenge and mark as complete with username ${claimChallengeRewardRequest.username} and challenge id ${claimChallengeRewardRequest.challengeId}")
            return null
        }

        if (userChallenge.isRewardClaimed) {
            LOGGER.error("Reward already claimed for username ${claimChallengeRewardRequest.username} and challenge id ${claimChallengeRewardRequest.challengeId}")
            return null
        }

        userChallenge.isRewardClaimed = true
        return userChallengeRepository.save(userChallenge)
    }

    @Transactional
    fun getCompletedChallengesTitleNames(user: User): List<String> {
        val completedChallenges = userChallengeRepository.findAllByIdAppUserAndIsRewardClaimedTrue(user.id)

        val titles = completedChallenges.mapNotNull { it.challenge?.title }.toMutableSet()

        val groupedChallenges = completedChallenges
            .groupBy { it.challenge?.level }

        val highestCompletedLevel = groupedChallenges
            .entries.lastOrNull { it.value.size == 3 }

        if (highestCompletedLevel != null) {
            val keys = groupedChallenges.keys.toList()
            val allStarTitles = keys.subList(0, keys.indexOf(highestCompletedLevel.key) +1)
                .map { "$it All Star" }
            titles.addAll(allStarTitles)
        }
        titles.add("Noob")
        return titles.toList().sorted()
    }

    @Transactional
    fun getUserChallengesDTO(username: String): List<UserChallengeDTO> {
        val userChallenges = userChallengeRepository.findAllByIdAppUserOrderByChallengeId(username)
        return userChallenges.mapNotNull {
            buildUserChallengeDTO(it)
        }
    }

    fun buildUserChallengeDTO(userChallenge: UserChallenge): UserChallengeDTO? {
        return userChallenge.challenge?.let { challenge ->
            UserChallengeDTO(
                challenge.id,
                challenge.name,
                challenge.title,
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
