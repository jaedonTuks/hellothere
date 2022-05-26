package hellothere.service

import hellothere.dto.leaderboards.TopThreeDto
import hellothere.dto.leaderboards.UserLeaderBoardDTO
import hellothere.model.feature.FF4jFeature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LeaderboardsService(
    private val featureService: FeatureService
) {
    fun getTopThreeUsers(): TopThreeDto? {
        if (featureService.isDisabled(FF4jFeature.LEADERBOARDS)) {
            LOGGER.info("Leaderboards feature disabled")
            return null
        }

        return TopThreeDto(
            getUserAtPosition(1),
            getUserAtPosition(2),
            getUserAtPosition(3)
        )
    }

    private fun getUserAtPosition(rank: Int): UserLeaderBoardDTO {
        return UserLeaderBoardDTO(
            "User $rank",
            "Noob",
            "Very good"
        )
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(LeaderboardsService::class.java)
    }
}
