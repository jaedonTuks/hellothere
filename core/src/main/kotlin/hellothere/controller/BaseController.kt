package hellothere.controller

import com.google.api.services.gmail.Gmail
import hellothere.service.google.GmailService
import hellothere.service.security.SecurityService
import javax.servlet.http.HttpServletRequest

abstract class BaseController(
    private val gmailService: GmailService,
    private val securityService: SecurityService
) {
    protected fun getUsernameAndClientFromRequest(request: HttpServletRequest): Pair<String, Gmail>? {
        val username = securityService.getUsernameFromRequest(request)
            ?: return null

        val client = gmailService.getGmailClientFromUsername(username)
            ?: return null

        return Pair(username, client)
    }
}
