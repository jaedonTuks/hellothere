package hellothere.controller

import hellothere.config.RestUrl.TEST
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(TEST)
class TestController {

    @GetMapping
    fun getControllerTest(): ResponseEntity<String> {
        return ResponseEntity.ok("Hello Gamified World!")
    }
}
