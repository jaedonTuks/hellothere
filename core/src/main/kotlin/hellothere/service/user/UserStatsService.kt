package hellothere.service.user

import hellothere.config.stats.StatsConfig
import hellothere.dto.UserChallengeDTO
import hellothere.dto.stats.MessageTotalsSummaryDTO
import hellothere.dto.user.WeekStatsDto
import hellothere.model.email.UserEmail
import hellothere.model.feature.FF4jFeature
import hellothere.model.stats.WeekStats
import hellothere.model.stats.category.*
import hellothere.model.user.User
import hellothere.repository.email.UserEmailRepository
import hellothere.repository.user.UserRepository
import hellothere.repository.user.WeekStatsRepository
import hellothere.requests.challenge.ClaimChallengeRewardRequest
import hellothere.service.FeatureService
import hellothere.service.challenge.ChallengeService
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
    private val challengeService: ChallengeService,
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
            ChallengeWeekStat(),
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
        messages: List<UserEmail> = listOf()
    ) {
        if (messages.isEmpty()) {
            LOGGER.error("Attempted to add xp to incorrect no available emails for user {$username} for category $statCategory. ids given [${messages.joinToString { it.gmailId }}]")
            return
        }

        val workingMessages = messages.filterNot {
            it.hasHadCategoryXpAllocated(statCategory)
        }.distinctBy { it.thread?.id?.threadId }

        if (workingMessages.isEmpty()) {
            LOGGER.info("Skipping add xp to for user {$username} in category $statCategory.Xp already allocated for message ids [${messages.joinToString { it.gmailId }}]")
            return
        }

        val moreXp = calculateActionXP(workingMessages, statCategory)
        challengeService.updateUserChallenges(username, statCategory)
        val weekStats = getOrCreateThisWeeksStats(username)
        updateStats(moreXp, username, statCategory, weekStats, messages)
    }

    @Transactional
    fun claimChallengeRewards(claimChallengeRewardRequest: ClaimChallengeRewardRequest): UserChallengeDTO? {
        if (featureService.isDisabled(FF4jFeature.CHALLENGES)) {
            return null
        }

        val userChallengeClaimed = challengeService.markUserChallengeAsClaimed(claimChallengeRewardRequest)
            ?: return null

        val weekStats = getOrCreateThisWeeksStats(claimChallengeRewardRequest.username)
        updateStats(
            userChallengeClaimed.challenge!!.reward,
            claimChallengeRewardRequest.username,
            StatCategory.CHALLENGE,
            weekStats,
            listOf()
        )
        return challengeService.buildUserChallengeDTO(userChallengeClaimed)
    }

    private fun updateStats(
        moreXp: Int,
        username: String,
        statCategory: StatCategory,
        weekStats: WeekStats?,
        messages: List<UserEmail>
    ) {
        if (weekStats?.addXP((moreXp), statCategory) == true) {
            weekStatsRepository.save(weekStats)
            markMessagesAsCompletedCategory(messages, statCategory)
            LOGGER.info("Finished adding $moreXp xp for to user {$username} for category $statCategory")
        } else {
            LOGGER.error("Attempted to add $moreXp xp to incorrect weekStats with id: [${weekStats?.id}] for user {$username} for category $statCategory")
        }
    }

    @Transactional
    fun getOrCreateThisWeeksStats(username: String): WeekStats? {
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

    private fun calculateActionXP(messages: List<UserEmail>, category: StatCategory): Int {
        val now = LocalDateTime.now()

        val actionConfig = when (category) {
            StatCategory.READ -> statsConfig.readConfig
            StatCategory.LABEL -> statsConfig.labelConfig
            StatCategory.REPLY -> statsConfig.replyConfig
            else -> StatsConfig.StatConfigItem()
        }

        var totalXp = actionConfig.xp * messages.size

        messages.forEach {
            if (now > getMaxActionTime(category, it.dateSent)) {
                totalXp -= if (now < it.dateSent.plusHours(statsConfig.cutOffHours)) {
                    actionConfig.penalty
                } else {
                    // todo mark message as past cutoff
                    actionConfig.cutOffPenalty
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
            else -> 0
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

    @Transactional
    fun markMessagesAsCompletedCategory(messages: List<UserEmail>, category: StatCategory) {
        if (messages.isEmpty()) {
            return
        }

        messages.forEach {
            it.markCompletedCategoryXP(category)
        }

        userEmailRepository.saveAll(messages)
    }

    @Transactional
    fun getMessageTotalsSummary(username: String): MessageTotalsSummaryDTO {
        return MessageTotalsSummaryDTO(
            totalEmailThreads = userEmailRepository.countDistinctByThreadUserId(username),
            totalEmails = userEmailRepository.countByThreadUserId(username),
            totalRead = userEmailRepository.countByThreadUserIdAndReadXpAllocatedDateNotNull(username),
            totalLabeled = userEmailRepository.countByThreadUserIdAndLabelXPAllocatedDateNotNull(username),
            totalReplied = userEmailRepository.countByThreadUserIdAndReplyXPAllocatedDateNotNull(username)
        )
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

    fun buildWeekStatsDtos(weeklyStats: MutableList<WeekStats>): List<WeekStatsDto> {
        return weeklyStats.mapNotNull { buildWeekStatsDto(it) }.sortedBy { it.startDate }
    }

    companion object {
        val LOGGER: Logger = org.slf4j.LoggerFactory.getLogger(UserStatsService::class.java)
    }
}
