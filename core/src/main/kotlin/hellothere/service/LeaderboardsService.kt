package hellothere.service

import hellothere.dto.leaderboards.TopThreeDto
import hellothere.dto.leaderboards.UserLeaderBoardDTO
import hellothere.dto.leaderboards.UserLeaderBoardGeneralDTO
import hellothere.model.feature.FF4jFeature
import hellothere.model.stats.WeekStats
import hellothere.model.user.User
import hellothere.repository.user.WeekStatsRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class LeaderboardsService(
    private val weekStatsRepository: WeekStatsRepository,
    private val featureService: FeatureService
) {

    @Transactional
    fun getCurrentPlaceOnLeaderboards(currentXp: Int): Long {
        return weekStatsRepository.findCountWithWithXpAbove(currentXp, LocalDate.now()) + 1
    }

    @Transactional
    fun getTopThreeUsers(): TopThreeDto? {
        if (featureService.isDisabled(FF4jFeature.LEADERBOARDS)) {
            LOGGER.info("Leaderboards feature disabled")
            return null
        }
        val allCurrentStats = weekStatsRepository.findAllByDateBetween(LocalDate.now())

        return TopThreeDto(
            getUserAtPosition(allCurrentStats.getOrNull(0)?.user, 0),
            getUserAtPosition(allCurrentStats.getOrNull(1)?.user, 1),
            getUserAtPosition(allCurrentStats.getOrNull(2)?.user, 2)
        )
    }

    @Transactional
    fun getAllLeaderBoardValues(): List<UserLeaderBoardGeneralDTO> {
        if (featureService.isDisabled(FF4jFeature.LEADERBOARDS)) {
            LOGGER.info("Leaderboards feature disabled")
            return listOf()
        }

        val allCurrentStats = weekStatsRepository.findAllByDateBetween(LocalDate.now())

        return allCurrentStats.mapIndexedNotNull { index, it -> buildUserLeaderboardGeneralDTO(it, index + 1) }
    }

    private fun buildUserLeaderboardGeneralDTO(weekStats: WeekStats, rank: Int): UserLeaderBoardGeneralDTO? {
        return weekStats.user?.leaderboardUsername?.let {
            UserLeaderBoardGeneralDTO(
                rank,
                weekStats.user!!.title,
                it,
                weekStats.getTotalExperience()
            )
        }
    }

    private fun getUserAtPosition(user: User?, position: Int): UserLeaderBoardDTO? {
        if (user == null) {
            LOGGER.info("No user found for position $position")
            if (position == -1) {
                return null
            }

            return UserLeaderBoardDTO(
                "Unclaimed",
                "",
                0
            )
        }

        return UserLeaderBoardDTO(
            user.leaderboardUsername,
            user.title,
            user.getCurrentWeeksStats()!!.getTotalExperience()
        )
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(LeaderboardsService::class.java)
    }
}
