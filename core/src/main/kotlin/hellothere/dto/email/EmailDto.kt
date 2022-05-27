package hellothere.dto.email

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class EmailDto(
    val id: String,
    val from: String,
    val date: LocalDateTime,
    val labelIds: List<String>,
    val subject: String,
    val snippet: String?
) {
    @JsonProperty("formattedDate")
    fun getFormattedDate(): String {
        return date.format(DateTimeFormatter.ofPattern("d MMMM HH:mm"))
    }
}
