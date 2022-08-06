package hellothere.service.user

import hellothere.dto.user.WeekStatsDto
import hellothere.model.feature.FF4jProperty
import hellothere.model.user.User
import hellothere.model.stats.WeekStats
import hellothere.model.stats.category.LabelWeekStat
import hellothere.model.stats.category.ReadWeekStat
import hellothere.model.stats.category.ReplyWeekStat
import hellothere.model.stats.category.WeekStatsCategory
import hellothere.repository.user.WeekStatsRepository
import hellothere.service.FeatureService
import org.slf4j.Logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.temporal.ChronoField

@Service
class UserStatsService(
    private val weekStatsRepository: WeekStatsRepository,
    private val featureService: FeatureService
) {
    @Transactional
    fun createNewWeekStatsForUser(user: User) {
        val zonedDateTime = ZonedDateTime.now()
        val lastStatsEndDate = user.weeklyStats.lastOrNull()?.endDate

        val startDate = lastStatsEndDate?.plusDays(1)
            ?: zonedDateTime.with(ChronoField.DAY_OF_WEEK, 1).toLocalDate()
        val endDate = startDate.plusDays(6)

        val newWeeklyStats = WeekStats(
            null,
            startDate,
            endDate,
            ReadWeekStat(),
            LabelWeekStat(),
            ReplyWeekStat(),
            user
        )

        weekStatsRepository.save(newWeeklyStats)
        user.addWeekStats(newWeeklyStats)
    }

    @Transactional
    fun updateUserStats(
        userName: String,
        statCategory: WeekStatsCategory.StatCategory,
        associatedMessageId: String
    ) {
        val moreXp = featureService.getProperty<Int>(FF4jProperty.ACTION_XP)
        val weekStats = weekStatsRepository.findFirstByUserIdAndDateBetween(userName, LocalDate.now())
        if (weekStats?.addXP(moreXp, statCategory) == true) {
            weekStatsRepository.save(weekStats)
            LOGGER.info("Finished adding $moreXp xp for to user {$userName} for category $statCategory")
        } else {
            LOGGER.error("Attempted to add $moreXp xp to incorrect weekStats with id: [${weekStats?.id}] for user {$userName} for category $statCategory")
        }
    }

    fun buildWeekStatsDto(weekStats: WeekStats?): WeekStatsDto? {
        return weekStats?.let {
            WeekStatsDto(
                it.startDate,
                it.endDate,
                it.getTotalExperience(),
                it.readWeekStat.experience,
                it.labelWeekStat.experience,
                it.replyWeekStat.experience
            )
        }
    }

    companion object {
        val LOGGER: Logger = org.slf4j.LoggerFactory.getLogger(UserStatsService::class.java)
    }
}
