package hellothere.controller

import hellothere.annotation.RequiresFeatureAspect
import hellothere.config.RestUrl.USER
import hellothere.dto.user.UserDto
import hellothere.service.user.UserService
import liquibase.pro.packaged.it
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(USER)
class UserController(
    private val userService: UserService
) {
    // todo add in jwt security for profile retrieval
    @GetMapping("/{username}")
    fun getUser(@PathVariable username: String): ResponseEntity<UserDto> {
        LOGGER.debug("Fetching user info for {$username}")
        return userService.getUser(username)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(RequiresFeatureAspect::class.java)
    }
}
