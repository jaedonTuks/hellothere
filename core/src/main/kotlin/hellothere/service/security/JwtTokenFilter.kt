package hellothere.service.security

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtTokenFilter(
    private val jwtTokenService: JwtTokenService
) : GenericFilterBean() {
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, filterChain: FilterChain?) {
        try {
            if (request is HttpServletRequest) {
                val secure = if (environment.activeProfiles.toList().contains("prod")) {
                    " Secure;"
                } else {
                    ""
                }

                val token = jwtTokenService.getTokenFromRequest(request)
                if (jwtTokenService.isTokenValid(token)) {
                    filterChain?.doFilter(request, response)
                    if (response is HttpServletResponse) {
                        response.setHeader("Set-Cookie", "${SecurityService.JWT_TOKEN_COOKIE_NAME}=$token; SameSite=strict; HttpOnly; Path=/; Max-Age=${JwtTokenService.TOKEN_LIFE_TIME};$secure")
                    }
                } else if (response is HttpServletResponse) {
                    response.setHeader(
                        "Set-Cookie",
                        "${SecurityService.JWT_TOKEN_COOKIE_NAME}=''; SameSite=strict; HttpOnly; SameSite=none; Path=/; Max-Age=-1;$secure"
                    )
                    response.sendRedirect("/login")
                }
            } else {
                throw Exception("Invalid servlet request. Expected type of httpServletRequest")
            }
        } catch (e: Exception) {
            LOGGER.error("An exception occurred when processing jwt. Returning unauthorized")
            LOGGER.debug(e.stackTraceToString())
        }
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(JwtTokenFilter::class.java)
    }
}
