package hellothere.service.security

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class JwtTokenFilter(
    private val jwtTokenService: JwtTokenService
) : GenericFilterBean() {
    override fun doFilter(request: ServletRequest?, response: ServletResponse?, filterChain: FilterChain?) {
        try {
            if (request is HttpServletRequest) {
                val token = jwtTokenService.getTokenFromRequest(request)
                if (jwtTokenService.isTokenValid(token)) {
                    filterChain?.doFilter(request, response)
                }
            } else {
                throw Exception("Invalid servlet request. Expected type of httpServletRequest")
            }
        } catch (e: Exception) {
            LOGGER.error("An exception occurred when processing jwt. Returning unauthorized")
            LOGGER.debug(e.stackTraceToString())
            // todo return unauthorised
        }
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(JwtTokenFilter::class.java)
    }
}
