package hellothere.service.user

import hellothere.dto.user.UserDto
import hellothere.model.user.User
import hellothere.repository.UserRepository
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
        return user?.let{
            UserDto(
                user.id,
                user.rank
            )
        }
    }
}
