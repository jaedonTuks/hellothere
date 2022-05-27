package hellothere.dto.email

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class EmailDto(
    val id: String,
    val from: String,
    val date: LocalDateTime,
    val labelIds: List<String>,
    val subject: String,
    val body: String?
) {
    @JsonProperty("formattedDate")
    fun getFormattedDate(): String {
        if (date.toLocalDate() == LocalDate.now()) {
            return date.format(DateTimeFormatter.ofPattern("HH:mm"))
        }

        return date.format(DateTimeFormatter.ofPattern("d MMMM"))
    }
}
