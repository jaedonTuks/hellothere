package hellothere.controller

import hellothere.config.RestUrl.GMAIL
import hellothere.dto.email.EmailDto
import hellothere.dto.email.EmailThreadDto
import hellothere.dto.email.EmailsContainerDTO
import hellothere.requests.email.ReplyRequest
import hellothere.requests.email.SendRequest
import hellothere.service.google.GmailService
import hellothere.service.google.GoogleAuthenticationService
import hellothere.service.security.SecurityService
import hellothere.service.user.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping(GMAIL)
class GmailController(
    private val googleAuthenticationService: GoogleAuthenticationService,
    private val gmailService: GmailService,
    private val userService: UserService,
    private val securityService: SecurityService
) : BaseController(gmailService, securityService) {

    @Value("\${url}")
    lateinit var currentUrl: String

    @GetMapping("/logout")
    fun logout(httpServletResponse: HttpServletResponse) {
        securityService.logout(httpServletResponse)
        httpServletResponse.status = HttpServletResponse.SC_FOUND
        httpServletResponse.setHeader("Location", "$currentUrl/#/login")
    }

    @GetMapping("/login")
    fun googleConnectionStatus(request: HttpServletRequest?): RedirectView? {
        return googleAuthenticationService.authorize()?.let { RedirectView(it) }
    }

    @GetMapping("/callback")
    fun oauth2Callback(
        @RequestParam(value = "code") code: String,
        httpServletResponse: HttpServletResponse
    ) {
        try {
            val (client, userAccessToken) = gmailService.getGmailClientFromCode(code)
            val user = securityService.loginOrSignup(client, httpServletResponse)

            if (userAccessToken.id == null) {
                userService.saveUserAccessToken(userAccessToken, user)
            }

            httpServletResponse.setHeader("Location", "$currentUrl")
            httpServletResponse.status = HttpServletResponse.SC_FOUND
        } catch (e: Exception) {
            LOGGER.error("An Error occurred with message: ${e.message}")
            LOGGER.debug(e.stackTraceToString())
            httpServletResponse.status = HttpServletResponse.SC_FOUND
            httpServletResponse.setHeader("Location", "$currentUrl/#/login?error=true")
        }
    }

    @GetMapping("/email/{id}")
    fun getFullEmailFromId(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @PathVariable id: String
    ): ResponseEntity<EmailThreadDto> {
        val (username, client) = getUsernameAndClientFromRequest(request, response)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val emailThread = gmailService.getFullEmailThreadData(client, username, id)

        return ResponseEntity.ok(emailThread)
    }

    @GetMapping("/attachment")
    fun getAndDownloadAttachment(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @RequestParam attachmentId: String,
        @RequestParam emailId: String
    ): ResponseEntity<ByteArray> {
        val (username, client) = getUsernameAndClientFromRequest(request, response)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val attachment = gmailService.getAttachment(client, username, attachmentId, emailId)

        return ResponseEntity
            .ok()
            .contentType(MediaType.IMAGE_JPEG)
            .body(attachment)
    }

    @GetMapping("/emails")
    fun getEmails(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @RequestParam pageToken: String? = null,
        @RequestParam searchString: String? = null,
        @RequestParam labels: List<String>? = listOf()
    ): ResponseEntity<EmailsContainerDTO> {
        val (username, client) = getUsernameAndClientFromRequest(request, response)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return ResponseEntity.ok(
            gmailService.getThreadsBaseData(
                client,
                username,
                searchString,
                labels ?: listOf(),
                pageToken
            )
        )
    }

    @PostMapping("/send", consumes = ["multipart/form-data"])
    fun sendEmail(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @ModelAttribute sendRequest: SendRequest
    ): ResponseEntity<EmailThreadDto> {
        val (username, client) = getUsernameAndClientFromRequest(request, response)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val email = gmailService.send(username, client, sendRequest)
            ?: return ResponseEntity.badRequest().build()

        return ResponseEntity.ok(email)
    }

    @PostMapping("/reply", consumes = ["multipart/form-data"])
    fun replyToEmail(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @ModelAttribute replyRequest: ReplyRequest
    ): ResponseEntity<EmailDto> {
        val (username, client) = getUsernameAndClientFromRequest(request, response)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val email = gmailService.sendReply(username, client, replyRequest)
            ?: return ResponseEntity.badRequest().build()
        return ResponseEntity.ok(email)
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(GmailController::class.java)
    }
}
