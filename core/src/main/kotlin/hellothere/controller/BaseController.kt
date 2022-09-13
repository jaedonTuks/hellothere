package hellothere.controller

import com.google.api.services.gmail.Gmail
import hellothere.service.google.GmailService
import hellothere.service.security.SecurityService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class BaseController(
    private val gmailService: GmailService,
    private val securityService: SecurityService
) {
    protected fun getUsernameAndClientFromRequest(request: HttpServletRequest, response: HttpServletResponse): Pair<String, Gmail>? {
        val username = getUsernameFromRequest(request, response) ?: return null

        val client = gmailService.getGmailClientFromUsername(username)
        if (client == null) {
            LOGGER.warn("No client for username $username")
            securityService.logout(response)
            return null
        }

        return Pair(username, client)
    }

    protected fun getUsernameFromRequest(request: HttpServletRequest, response: HttpServletResponse): String? {
        val username = securityService.getUsernameFromRequest(request)
        if (username == null) {
            securityService.logout(response)
            return null
        }

        return username
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(GmailController::class.java)
    }
}
