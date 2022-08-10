package hellothere.controller

import hellothere.annotation.RequiresFeatureAspect
import hellothere.config.RestUrl.CHALLENGE
import hellothere.dto.UserChallengeDTO
import hellothere.requests.challenge.ClaimChallengeRewardRequest
import hellothere.service.challenge.ChallengeService
import hellothere.service.security.SecurityService
import hellothere.service.user.UserStatsService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(CHALLENGE)
class ChallengeController(
    private val securityService: SecurityService,
    private val challengeService: ChallengeService,
    private val userStatsService: UserStatsService,
) {
    @GetMapping
    fun getUserChallenges(
        request: HttpServletRequest
    ): ResponseEntity<List<UserChallengeDTO>> {
        val username = securityService.getUsernameFromRequest(request)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        LOGGER.debug("Fetching user challenges for {$username}")

        return ResponseEntity.ok(challengeService.getUserChallengesDTO(username))
    }

    @GetMapping("/claim-reward/{id}")
    fun sendEmail(
        request: HttpServletRequest,
        @PathVariable id: Long
    ): ResponseEntity<UserChallengeDTO> {
        val username = securityService.getUsernameFromRequest(request)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val claimedChallengeDto = userStatsService.claimChallengeRewards(
            ClaimChallengeRewardRequest(
                username,
                id
            )
        )

        return claimedChallengeDto?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.noContent().build()
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(RequiresFeatureAspect::class.java)
    }
}
