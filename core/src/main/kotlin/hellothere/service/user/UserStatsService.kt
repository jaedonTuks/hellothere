package hellothere.service.user

import hellothere.config.stats.StatsConfig
import hellothere.dto.user.WeekStatsDto
import hellothere.model.feature.FF4jProperty
import hellothere.model.stats.WeekStats
import hellothere.model.stats.category.*
import hellothere.model.user.User
import hellothere.repository.email.UserEmailRepository
import hellothere.repository.user.UserRepository
import hellothere.repository.user.WeekStatsRepository
import hellothere.service.FeatureService
import org.slf4j.Logger
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoField

@Service
class UserStatsService(
    private val userRepository: UserRepository,
    private val userEmailRepository: UserEmailRepository,
    private val weekStatsRepository: WeekStatsRepository,
    private val featureService: FeatureService,
    private val statsConfig: StatsConfig
) {
    @Transactional
    fun createNewWeekStatsForUser(user: User): WeekStats {

        LOGGER.info("Creating new week stats for user: ${user.id}")
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

        val savedWeekStats = weekStatsRepository.save(newWeeklyStats)
        user.addWeekStats(newWeeklyStats)
        return savedWeekStats
    }

    @Transactional
    fun updateUserStats(
        username: String,
        statCategory: StatCategory,
        associatedMessageIds: List<Long> = listOf()
    ) {
        val moreXp: Int = when (statCategory) {
            StatCategory.READ -> calculateReadXP(associatedMessageIds)
            StatCategory.REPLY,
            StatCategory.LABEL -> featureService.getProperty(FF4jProperty.ACTION_XP)
        }

        val weekStats = getOrCreateThisWeeksStats(username)
        if (weekStats?.addXP(moreXp, statCategory) == true) {
            weekStatsRepository.save(weekStats)
            LOGGER.info("Finished adding $moreXp xp for to user {$username} for category $statCategory")
        } else {
            LOGGER.error("Attempted to add $moreXp xp to incorrect weekStats with id: [${weekStats?.id}] for user {$username} for category $statCategory")
        }
    }

    @Transactional
    fun getOrCreateThisWeeksStats(username: String): WeekStats? {
        // todo what happens if the user interacted last 2 weeks ago
        val weekStats = weekStatsRepository.findFirstByUserIdAndDateBetween(username, LocalDate.now())

        if (weekStats != null) {
            return weekStats
        }
        val user = userRepository.findByIdOrNull(username)
        if (user == null) {
            LOGGER.info("Cannot create new week stats for user $username, no user found")
        }
        return user?.let { createNewWeekStatsForUser(it) }
    }

    // todo transactional?
    private fun calculateReadXP(associatedMessageIds: List<Long>): Int {
        val messages = userEmailRepository.findAllByIdIn(associatedMessageIds)

        if (messages.isEmpty()) {
            LOGGER.error("No emails available for calculating read xp, ids given [$associatedMessageIds]")
            return 0
        }
        val now = LocalDateTime.now()

        var totalXp = statsConfig.readConfig.xp * messages.size

        messages.forEach {
            if (now > getMaxActionTime(StatCategory.READ, it.dateSent)) {
                totalXp -= if (now < it.dateSent.plusHours(statsConfig.cutOffHours)) {
                    statsConfig.readConfig.penalty
                } else {
                    // todo mark message as past cutoff
                    statsConfig.readConfig.cutOffPenalty
                }
            }
        }

        return totalXp
    }

    fun getMaxActionTime(category: StatCategory, dateSent: LocalDateTime): LocalDateTime {
        val timeOfSent = dateSent.toLocalTime()

        val leniencyMinutes = when (category) {
            StatCategory.READ -> statsConfig.readConfig.leniencyMinutes
            StatCategory.LABEL -> statsConfig.labelConfig.leniencyMinutes
            StatCategory.REPLY -> statsConfig.replyConfig.leniencyMinutes
        }

        val startWorkingTime = LocalTime.parse(statsConfig.workingStartTime)
        val endWorkingTime = LocalTime.parse(statsConfig.workingEndTime)

        val reasonableActionTime = if (timeOfSent < startWorkingTime || timeOfSent > endWorkingTime) {
            startWorkingTime.atDate(dateSent.plusDays(1).toLocalDate())
        } else {
            dateSent
        }

        return reasonableActionTime.plusMinutes(leniencyMinutes)
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
