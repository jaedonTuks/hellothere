package hellothere.dto.email

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class EmailThreadDto(
    val id: String,
    val subject: String,
    val from: String,
    val latestDate: LocalDateTime?,
    val labelIds: List<String>,
    val emails: List<EmailDto>,
) {
    @JsonProperty("formattedDate")
    fun getFormattedDate(): String? {
        if (latestDate?.toLocalDate() == LocalDate.now()) {
            return latestDate?.format(DateTimeFormatter.ofPattern("HH:mm"))
        }

        return latestDate?.format(DateTimeFormatter.ofPattern("d MMMM"))
    }
}
