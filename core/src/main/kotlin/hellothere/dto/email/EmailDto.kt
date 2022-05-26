package hellothere.dto.email

import java.time.LocalDateTime

data class EmailDto(
    val id: String,
    val from: String,
    val date: LocalDateTime,
    val labelIds: List<String>,
    val subject: String,
    val snippet: String?
)
