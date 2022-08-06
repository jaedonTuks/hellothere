package hellothere.dto.user

import java.time.LocalDate

data class WeekStatsDto(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val experience: Int = 0,
    val readExperience: Int = 0,
    val labelExperience: Int = 0,
    val replyExperience: Int = 0
)
