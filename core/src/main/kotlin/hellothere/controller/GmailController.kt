package hellothere.controller

import com.google.api.services.gmail.model.Message
import hellothere.config.RestUrl.GMAIL
import hellothere.dto.email.EmailDto
import hellothere.service.gmail.GmailService
import hellothere.service.security.SecurityService
import hellothere.service.user.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping(GMAIL)
class GmailController(
    private val gmailService: GmailService,
    private val userService: UserService,
    private val securityService: SecurityService
) {

    @GetMapping("/login")
    fun googleConnectionStatus(request: HttpServletRequest?): RedirectView? {
        return gmailService.authorize()?.let { RedirectView(it) }
    }

    @GetMapping("/callback")
    fun oauth2Callback(
        @RequestParam(value = "code") code: String,
        httpServletResponse: HttpServletResponse
    ) {
        try {
            val (client, userAccessToken) = gmailService.getGmailClientFromCode(code)
            val user = securityService.loginOrSignup(client, httpServletResponse)

            if (userAccessToken.user == null) {
                userService.saveUserAccessToken(userAccessToken, user)
            }

            httpServletResponse.setHeader("Location", "http://localhost:8080/profile")
            httpServletResponse.status = HttpServletResponse.SC_FOUND
        } catch (e: Exception) {
            LOGGER.error("An Error occurred with message: ${e.message}")
            LOGGER.debug(e.stackTraceToString())
            httpServletResponse.status = HttpServletResponse.SC_FOUND
            httpServletResponse.setHeader("Location", "http://localhost:8080/login?error=true")
        }
    }

    @GetMapping("/emails")
    fun getEmails(request: HttpServletRequest): ResponseEntity<List<EmailDto>> {
        val username = securityService.getUsernameFromRequest(request)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val client = gmailService.getGmailClientFromUsername(username)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val emails = gmailService.getEmailsBaseData(client)

        return ResponseEntity.ok(emails)
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(GmailController::class.java)
    }
}
