package hellothere.service.user

import com.google.api.services.gmail.Gmail
import hellothere.dto.user.UserDto
import hellothere.dto.user.WeekStatsDto
import hellothere.model.user.Rank
import hellothere.model.user.User
import hellothere.model.user.UserAccessToken
import hellothere.model.user.WeekStats
import hellothere.repository.UserAccessTokenRepository
import hellothere.repository.UserRepository
import hellothere.repository.WeekStatsRepository
import hellothere.service.gmail.GmailService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.time.temporal.ChronoField

@Service
class UserService(
    private val userRepository: UserRepository,
    private val weekStatsRepository: WeekStatsRepository,
    private val userAccessTokenRepository: UserAccessTokenRepository,
    private val gmailService: GmailService,
) {
    fun getUserById(username: String): User? {
        return userRepository.findByIdOrNull(username)
    }

    fun getUserDto(username: String): UserDto? {
        val user = getUserById(username)
        return buildUserDto(user)
    }

    fun buildUserDto(user: User?): UserDto? {
        return user?.let {
            UserDto(
                it.id,
                it.rank,
                buildWeekStatsDto(it.getCurrentWeeksStats())
            )
        }
    }

    fun buildWeekStatsDto(weekStats: WeekStats?): WeekStatsDto? {
        return weekStats?.let {
            WeekStatsDto(
                it.experience,
                it.startDate,
                it.endDate
            )
        }
    }

    fun loginOrSignup(client: Gmail): User? {
        val gmailProfile = gmailService.getGmailUserInfo(client)

        return getUserById(gmailProfile.emailAddress)
            ?: signupNewUser(gmailProfile.emailAddress)
    }

    @Transactional
    fun signupNewUser(newUserId: String): User? {
        val zonedDateTime = ZonedDateTime.now()
        val firstOfWeek: ZonedDateTime =
            zonedDateTime.with(ChronoField.DAY_OF_WEEK, 1) // ISO 8601, Monday is first day of week.
        val firstOfNextWeek = firstOfWeek.plusWeeks(1).minusDays(1)

        val newWeeklyStats = WeekStats(
            null,
            0,
            firstOfWeek.toLocalDate(),
            firstOfNextWeek.toLocalDate()
        )
        val newUser = User(
            newUserId,
            Rank.NOOB,
            0,
            listOf(newWeeklyStats)
        )

        userRepository.save(newUser)
        weekStatsRepository.save(newWeeklyStats)

        return newUser
    }

    fun saveUserAccessToken(userAccessToken: UserAccessToken, user: User?) {
        if (user == null) {
            LOGGER.info("Null user. Skipping saving of token")
            return
        }

        userAccessToken.user = user
        userAccessTokenRepository.save(userAccessToken)
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(UserService::class.java)
    }
}
