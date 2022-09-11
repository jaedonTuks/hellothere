package hellothere.controller

import hellothere.config.RestUrl.LABEL
import hellothere.dto.label.LabelContainerDTO
import hellothere.dto.label.LabelDto
import hellothere.dto.label.LabelUpdateDto
import hellothere.requests.label.UpdateEmailLabelsRequest
import hellothere.requests.label.UpdateLabelColorRequest
import hellothere.requests.label.UpdateLabelViewableRequest
import hellothere.service.google.GmailService
import hellothere.service.label.LabelService
import hellothere.service.security.SecurityService
import liquibase.pro.packaged.it
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping(LABEL)
class LabelController(
    private val gmailService: GmailService,
    private val labelService: LabelService,
    securityService: SecurityService,
) : BaseController(gmailService, securityService) {

    @GetMapping
    fun getLabels(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<LabelContainerDTO> {
        val (username, client) = getUsernameAndClientFromRequest(request, response)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        val labels = labelService.getLabels(client, username)
        return ResponseEntity.ok(labels)
    }

    @PostMapping("/update")
    fun updateEmailLabels(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @RequestBody updateLabelRequest: UpdateEmailLabelsRequest
    ): ResponseEntity<LabelUpdateDto> {
        val (username, client) = getUsernameAndClientFromRequest(request, response)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return labelService.updateLabels(username, client, updateLabelRequest)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
    }

    @PostMapping("/is-viewable/update")
    fun updateLabelIsViewable(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @RequestBody updateLabelRequest: UpdateLabelViewableRequest
    ): ResponseEntity<LabelDto> {
        val username = getUsernameFromRequest(request, response)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return labelService.updateLabelIsViewable(username, updateLabelRequest)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
    }

    @PostMapping("/color/update")
    fun updateLabelColor(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @RequestBody updateLabelRequest: UpdateLabelColorRequest
    ): ResponseEntity<LabelDto> {
        val username = getUsernameFromRequest(request, response)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return labelService.updateLabelColor(username, updateLabelRequest)?.let {
            ResponseEntity.ok(it)
        } ?: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(LabelController::class.java)
    }
}
