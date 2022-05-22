package hellothere.controller

import hellothere.config.RestUrl.GMAIL
import hellothere.service.gmail.GmailService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(GMAIL)
class GmailController(
    private val gmailService: GmailService
) {

    @GetMapping("/login")
    fun googleConnectionStatus(request: HttpServletRequest?): RedirectView? {
        // todo handle redirect if user is already logged in a bit differently
        return gmailService.authorize()?.let { RedirectView(it) }
    }

    @GetMapping("/callback")
    fun oauth2Callback(@RequestParam(value = "code") code: String): ResponseEntity<String> {
        try {
            val client = gmailService.getGmailClientFromCode(code)
            gmailService.getEmailIdList(client)
            println("test point")
        } catch (e: Exception) {
            LOGGER.error("An Error occured with message: ${e.message}")
            LOGGER.debug(e.toString())
        }

        return ResponseEntity.ok("Completed")
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(GmailController::class.java)
    }
}
