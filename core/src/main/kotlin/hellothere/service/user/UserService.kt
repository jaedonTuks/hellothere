package hellothere.service.user

import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Profile
import hellothere.dto.user.UserDto
import hellothere.dto.user.WeekStatsDto
import hellothere.model.user.Rank
import hellothere.model.user.User
import hellothere.model.user.UserAccessToken
import hellothere.model.user.WeekStats
import hellothere.repository.user.UserAccessTokenRepository
import hellothere.repository.user.UserRepository
import hellothere.repository.user.WeekStatsRepository
import hellothere.service.google.GmailService
import hellothere.service.label.LabelService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import java.time.temporal.ChronoField

@Service
class UserService(
    private val labelService: LabelService,
    private val userRepository: UserRepository,
    private val weekStatsRepository: WeekStatsRepository,
    private val userAccessTokenRepository: UserAccessTokenRepository,
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
        val gmailProfile = getGmailUserInfo(client)

        return getUserById(gmailProfile.emailAddress)
            ?: signupNewUser(gmailProfile.emailAddress, client)
    }

    @Transactional
    fun signupNewUser(newUserId: String, client: Gmail): User? {
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

        newWeeklyStats.user = newUser

        userRepository.save(newUser)
        weekStatsRepository.save(newWeeklyStats)

        labelService.getLabels(client, newUserId)

        return newUser
    }

    fun saveUserAccessToken(userAccessToken: UserAccessToken, user: User?) {
        if (user?.id == null) {
            LOGGER.info("Null user or id. Skipping saving of token")
            return
        }

        userAccessToken.id = user.id
        userAccessTokenRepository.save(userAccessToken)
    }

    fun getGmailUserInfo(client: Gmail): Profile {
        LOGGER.info("Fetching user info for client $client")

        return client
            .users()
            .getProfile(GmailService.USER_SELF_ACCESS)
            .execute()
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(UserService::class.java)
    }
}
