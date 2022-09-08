package hellothere.controller

import hellothere.config.RestUrl.FEATURE
import hellothere.config.RestUrl.GMAIL
import hellothere.dto.email.EmailDto
import hellothere.dto.email.EmailThreadDto
import hellothere.dto.email.EmailsContainerDTO
import hellothere.model.feature.FF4jFeature
import hellothere.requests.email.ReplyRequest
import hellothere.requests.email.SendRequest
import hellothere.service.FeatureService
import hellothere.service.google.GmailService
import hellothere.service.google.GoogleAuthenticationService
import hellothere.service.security.SecurityService
import hellothere.service.user.UserService
import liquibase.pro.packaged.it
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping(FEATURE)
class FeatureController(
    private val featureService: FeatureService
) {
    @GetMapping("/gamification-view-enabled")
    fun isGamificationViewsEnabled(httpServletResponse: HttpServletResponse): ResponseEntity<Boolean> {
        return ResponseEntity.ok(featureService.isEnabled(FF4jFeature.GAMIFICATION_VIEWS))
    }

    @GetMapping("/clear-cache")
    fun clearCache(httpServletResponse: HttpServletResponse): ResponseEntity<String> {
        featureService.clearCaches()
        return ResponseEntity.ok("Cache cleared")
    }
}
