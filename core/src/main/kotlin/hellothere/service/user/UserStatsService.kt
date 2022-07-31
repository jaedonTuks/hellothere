package hellothere.service.user

import hellothere.model.feature.FF4jProperty
import hellothere.repository.user.WeekStatsRepository
import hellothere.service.FeatureService
import org.slf4j.Logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class UserStatsService(
    private val weekStatsRepository: WeekStatsRepository,
    private val featureService: FeatureService
) {

    @Transactional
    fun updateUserStats(userName: String) {
        val moreXp = featureService.getProperty<Int>(FF4jProperty.ACTION_XP)
        val weekStats = weekStatsRepository.findFirstByUserIdAndDateBetween(userName, LocalDate.now())
        if (weekStats?.addXP(moreXp) == true) {
            weekStatsRepository.save(weekStats)
            LOGGER.info("Finished adding $moreXp xp for to user {$userName}")
        } else {
            LOGGER.error("Attempted to add $moreXp xp to incorrect weekStats with id: [${weekStats?.id}] for user {$userName}")
        }

        println("Test")
    }

    companion object {
        val LOGGER: Logger = org.slf4j.LoggerFactory.getLogger(UserStatsService::class.java)
    }
}
