package hellothere.service.security

import com.google.api.services.gmail.Gmail
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

    fun loginOrSignup(client: Gmail, response: HttpServletResponse) {
        val user = userService.loginOrSignup(client)
        val token = jwtTokenService.createUserToken(user)
        response.setHeader("Set-Cookie", "$JWT_TOKEN_COOKIE_NAME=$token; Path=/; Max-Age=60; HttpOnly;")
    }

    companion object {
        val LOGGER: Logger = org.slf4j.LoggerFactory.getLogger(SecurityService::class.java)
        const val JWT_TOKEN_COOKIE_NAME = "jwt-token"
    }
}
