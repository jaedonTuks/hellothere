package hellothere.service.security

import com.google.api.services.gmail.Gmail
import hellothere.model.user.User
import hellothere.service.security.JwtTokenService.Companion.TOKEN_LIFE_TIME
import hellothere.service.user.UserService
import org.slf4j.Logger
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class SecurityService(
    private val environment: Environment,
    private val jwtTokenService: JwtTokenService,
    private val userService: UserService
) {
    fun getUsernameFromRequest(request: HttpServletRequest): String? {
        val token = jwtTokenService.getTokenFromRequest(request)
        LOGGER.info("Fetching username for request to : ${request.requestURL}")
        val decodedToken = jwtTokenService.decodeToken(token)
        return decodedToken?.username
    }

    fun loginOrSignup(client: Gmail, response: HttpServletResponse): User? {
        val user = userService.loginOrSignup(client)
        val userDto = userService.buildUserDto(user)
        val token = jwtTokenService.createUserToken(userDto)

        val secure = if (environment.activeProfiles.toList().contains("prod")) {
            " Secure;"
        } else {
            ""
        }
        response.setHeader("Set-Cookie", "$JWT_TOKEN_COOKIE_NAME=$token; SameSite=strict; HttpOnly; Path=/; Max-Age=$TOKEN_LIFE_TIME;$secure")

        return user
    }

    fun logout(response: HttpServletResponse) {
        val secure = if (environment.activeProfiles.toList().contains("prod")) {
            " Secure;"
        } else {
            ""
        }
        response.setHeader("Set-Cookie", "$JWT_TOKEN_COOKIE_NAME='';  SameSite=strict; HttpOnly; Path=/; Max-Age=-1;$secure")
    }

    companion object {
        val LOGGER: Logger = org.slf4j.LoggerFactory.getLogger(SecurityService::class.java)
        const val JWT_TOKEN_COOKIE_NAME = "jwt-token"
    }
}
