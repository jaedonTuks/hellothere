package hellothere.controller

import hellothere.config.RestUrl.LABEL
import hellothere.dto.label.LabelDto
import hellothere.dto.label.LabelUpdateDto
import hellothere.requests.label.UpdateLabelsRequest
import hellothere.service.google.GmailService
import hellothere.service.label.LabelService
import hellothere.service.security.SecurityService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping(LABEL)
class LabelController(
    private val gmailService: GmailService,
    private val labelService: LabelService,
    securityService: SecurityService,
) : BaseController(gmailService, securityService) {

    @GetMapping
    fun getLabels(request: HttpServletRequest): ResponseEntity<List<LabelDto>> {
        val (username, client) = getUsernameAndClientFromRequest(request)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val labels = labelService.getLabels(client, username)
        return ResponseEntity.ok(labels)
    }

    @PostMapping("/update")
    fun updateLabels(
        request: HttpServletRequest,
        @RequestBody updateLabelRequest: UpdateLabelsRequest
    ): ResponseEntity<LabelUpdateDto> {
        val (username, client) = getUsernameAndClientFromRequest(request)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return labelService.updateLabels(username, client, updateLabelRequest)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(LabelController::class.java)
    }
}
