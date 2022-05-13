package hellothere.controller

import hellothere.config.RestUrl.USER
import hellothere.dto.user.UserDto
import hellothere.service.user.UserService
import liquibase.pro.packaged.it
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
        return userService.getUser(username)?.let{
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }
}
