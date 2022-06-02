package hellothere.controller

import hellothere.annotation.RequiresFeatureAspect
import hellothere.config.RestUrl.USER
import hellothere.dto.user.UserDto
import hellothere.service.security.SecurityService
import hellothere.service.user.UserService
import liquibase.pro.packaged.it
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
