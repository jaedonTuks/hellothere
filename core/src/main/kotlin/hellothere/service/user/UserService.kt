package hellothere.service.user

import hellothere.dto.user.UserDto
import hellothere.dto.user.WeekStatsDto
import hellothere.model.user.User
import hellothere.model.user.WeekStats
import hellothere.repository.UserRepository
import liquibase.pro.packaged.it
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun getUserById(username: String): User? {
        return userRepository.findByIdOrNull(username)
    }

    fun getUser(username: String): UserDto? {
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
}
