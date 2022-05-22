package hellothere.controller

import hellothere.config.RestUrl.GMAIL
import hellothere.service.gmail.GmailService
import hellothere.service.user.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping(GMAIL)
class GmailController(
    private val gmailService: GmailService,
    private val userService: UserService
) {

    @GetMapping("/login")
    fun googleConnectionStatus(request: HttpServletRequest?): RedirectView? {
        // todo handle redirect if user is already logged in a bit differently
        return gmailService.authorize()?.let { RedirectView(it) }
    }

    @GetMapping("/callback")
    fun oauth2Callback(
        @RequestParam(value = "code") code: String,
        httpServletResponse: HttpServletResponse
    ) {
        try {
            val client = gmailService.getGmailClientFromCode(code)
            userService.loginOrSignup(client)

            // todo add the jwt token for access below
            httpServletResponse.setHeader("Set-Cookie", "testCookie=testValue; Path=/; Max-Age=60;")
            httpServletResponse.setHeader("Location", "http://localhost:8080/profile")
            httpServletResponse.status = HttpServletResponse.SC_FOUND
        } catch (e: Exception) {
            LOGGER.error("An Error occurred with message: ${e.message}")
            LOGGER.debug(e.toString())
            httpServletResponse.status = HttpServletResponse.SC_FOUND
            httpServletResponse.setHeader("Location", "http://localhost:8080/login?error=true")
        }
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(GmailController::class.java)
    }
}
