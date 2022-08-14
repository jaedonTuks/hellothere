package hellothere.dto.email

import com.fasterxml.jackson.annotation.JsonProperty
import hellothere.model.email.EnrichedLabel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class EmailThreadDto(
    val id: String,
    val subject: String,
    val from: String,
    val latestDate: LocalDateTime?,
    val originalLabelIds: List<String>,
    val emails: List<EmailDto>,
) {

    @JsonProperty("labelIds")
    fun getEnrichedLabelIds(): List<String> {
        val enrichedLabelIds = mutableListOf<String>()
        enrichedLabelIds.addAll(originalLabelIds)

        if (from.contains("no") && from.contains("reply")) {
            enrichedLabelIds.add(EnrichedLabel.NO_REPLY.value)
        }

        // todo come back later
        enrichedLabelIds.filter { !it.lowercase().contains("category_") }

        return enrichedLabelIds
    }

    @JsonProperty("instantSent")
    fun getInstantSent(): Long? {
        return latestDate?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
    }

    @JsonProperty("formattedDate")
    fun getFormattedDate(): String? {
        if (latestDate?.toLocalDate() == LocalDate.now()) {
            return latestDate?.format(DateTimeFormatter.ofPattern("HH:mm"))
        }

        return latestDate?.format(DateTimeFormatter.ofPattern("d MMMM"))
    }
}
