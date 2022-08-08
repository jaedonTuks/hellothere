package hellothere.service.user

import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Profile
import hellothere.dto.user.UserDto
import hellothere.model.user.Rank
import hellothere.model.user.User
import hellothere.model.user.UserAccessToken
import hellothere.repository.user.UserAccessTokenRepository
import hellothere.repository.user.UserRepository
import hellothere.service.google.GmailService
import hellothere.service.label.LabelService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val labelService: LabelService,
    private val userRepository: UserRepository,
    private val userStatsService: UserStatsService,
    private val userAccessTokenRepository: UserAccessTokenRepository,
) {
    fun getUserById(username: String): User? {
        return userRepository.findByIdOrNull(username)
    }

    fun getUserDto(username: String): UserDto? {
        val user = getUserById(username)
        return buildUserDto(user)
    }

    @Transactional
    fun buildUserDto(user: User?): UserDto? {
        return user?.let {
            UserDto(
                it.id,
                it.rank,
                userStatsService.buildWeekStatsDto(it.getCurrentWeeksStats()),
                userStatsService.buildWeekStatsDtos(it.weeklyStats),
                userStatsService.getMessageTotalsSummary(user.id),
                it.getTotalExperience()
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
        val newUser = User(
            newUserId,
            Rank.NOOB
        )
        userRepository.save(newUser)
        userStatsService.createNewWeekStatsForUser(newUser)
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
