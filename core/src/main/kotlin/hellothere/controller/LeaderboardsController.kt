package hellothere.controller

import hellothere.config.RestUrl.LEADERBOARDS
import hellothere.dto.leaderboards.TopThreeDto
import hellothere.dto.leaderboards.UserLeaderBoardDTO
import hellothere.dto.leaderboards.UserLeaderBoardGeneralDTO
import hellothere.service.LeaderboardsService
import liquibase.pro.packaged.it
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(LEADERBOARDS)
class LeaderboardsController(
    private val leaderboardsService: LeaderboardsService
) {

    @GetMapping("/top-three")
    fun getTop3(): ResponseEntity<TopThreeDto> {
        val topThree = leaderboardsService.getTopThreeUsers()

        return topThree?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.status(HttpStatus.FORBIDDEN).build()
    }

    @GetMapping("/full-leaderboard")
    fun getAllUsers(): ResponseEntity<List<UserLeaderBoardGeneralDTO>> {
        return ResponseEntity.ok(leaderboardsService.getAllLeaderBoardValues())
    }
}
