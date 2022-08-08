package hellothere.controller

import hellothere.annotation.RequiresFeatureAspect
import hellothere.config.RestUrl.USER
import hellothere.dto.user.UserDto
import hellothere.requests.user.UpdateUsernameRequest
import hellothere.service.security.SecurityService
import hellothere.service.user.UserService
import liquibase.pro.packaged.it
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
    private val securityService: SecurityService
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

        return ResponseEntity.ok(username != null)
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(RequiresFeatureAspect::class.java)
    }
}
