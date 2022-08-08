package hellothere.service.security

import com.google.api.services.gmail.Gmail
import hellothere.model.user.User
import hellothere.service.user.UserService
import org.slf4j.Logger
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class SecurityService(
    private val jwtTokenService: JwtTokenService,
    private val userService: UserService
) {
    fun getUsernameFromRequest(request: HttpServletRequest): String? {
        val token = jwtTokenService.getTokenFromRequest(request)
        val decodedToken = jwtTokenService.decodeToken(token)
        return decodedToken?.username
    }

    fun loginOrSignup(client: Gmail, response: HttpServletResponse): User? {
        val user = userService.loginOrSignup(client)
        val userDto = userService.buildUserDto(user)
        val token = jwtTokenService.createUserToken(userDto)
        response.setHeader("Set-Cookie", "$JWT_TOKEN_COOKIE_NAME=$token; Path=/; Max-Age=$JWT_TOKEN_EXPIRY_AGE; HttpOnly;")

        return user
    }

    fun logout(response: HttpServletResponse) {
        response.setHeader("Set-Cookie", "$JWT_TOKEN_COOKIE_NAME=''; Path=/; Max-Age=0; HttpOnly;")
    }

    companion object {
        val LOGGER: Logger = org.slf4j.LoggerFactory.getLogger(SecurityService::class.java)
        const val JWT_TOKEN_COOKIE_NAME = "jwt-token"
        const val JWT_TOKEN_EXPIRY_AGE = 3600000
    }
}
