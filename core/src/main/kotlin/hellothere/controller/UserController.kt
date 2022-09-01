package hellothere.controller

import hellothere.annotation.RequiresFeatureAspect
import hellothere.config.RestUrl.USER
import hellothere.dto.UserChallengeDTO
import hellothere.dto.user.NotificationTokenDTO
import hellothere.dto.user.UserDto
import hellothere.requests.user.UpdateNotificationToken
import hellothere.requests.user.UpdateUsernameRequest
import hellothere.service.NotificationService
import hellothere.service.challenge.ChallengeService
import hellothere.service.security.SecurityService
import hellothere.service.user.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(USER)
class UserController(
    private val userService: UserService,
    private val challengeService: ChallengeService,
    private val securityService: SecurityService,
    private val notificationService: NotificationService
) {
    @GetMapping
    fun getUser(
        request: HttpServletRequest
    ): ResponseEntity<UserDto> {
        val username = securityService.getUsernameFromRequest(request)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        LOGGER.debug("Fetching user info for {$username}")

        return userService.getUserDto(username)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/challenges")
    fun getUserChallenges(
        request: HttpServletRequest
    ): ResponseEntity<List<UserChallengeDTO>> {
        val username = securityService.getUsernameFromRequest(request)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        LOGGER.debug("Fetching user challenges for {$username}")

        return ResponseEntity.ok(challengeService.getUserChallengesDTO(username))
    }

    @PostMapping("/edit-username")
    fun sendEmail(
        request: HttpServletRequest,
        @RequestBody updateUsernameRequest: UpdateUsernameRequest
    ): ResponseEntity<String> {
        val username = securityService.getUsernameFromRequest(request)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        return userService.updateLeaderboardUsername(username, updateUsernameRequest)?.let {
            ResponseEntity.ok(it.leaderboardUsername)
        } ?: ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }

    @GetMapping("/isLoggedIn")
    fun isLoggedIn(
        request: HttpServletRequest
    ): ResponseEntity<Boolean> {
        val username = securityService.getUsernameFromRequest(request)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return ResponseEntity.ok(username != null)
    }

    @GetMapping("/get-notification-token")
    fun getNotificationServiceKey(
        request: HttpServletRequest
    ): ResponseEntity<NotificationTokenDTO?> {
        val username = securityService.getUsernameFromRequest(request)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        LOGGER.info("Updating $username firebase token")
        return ResponseEntity.ok(notificationService.getUserNotificationToken(username))
    }

    @PostMapping("/update-notification-token")
    fun updateMessageToken(
        request: HttpServletRequest,
        @RequestBody updateNotificationToken: UpdateNotificationToken
    ): ResponseEntity<Boolean> {
        val username = securityService.getUsernameFromRequest(request)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        userService.updateNotificationToken(username, updateNotificationToken.token)
        return ResponseEntity.ok(true)
    }

    @GetMapping("/sendTestMessage")
    fun sendTest(
        request: HttpServletRequest,
        @RequestParam username: String
    ): ResponseEntity<Boolean> {

        notificationService.checkForNewEmails()

        return ResponseEntity.ok(true)
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(RequiresFeatureAspect::class.java)
    }
}
