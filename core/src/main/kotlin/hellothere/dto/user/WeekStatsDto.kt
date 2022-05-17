package hellothere.dto.user

import java.time.LocalDate

data class WeekStatsDto(
    val experience: Int = 0,
    val startDate: LocalDate,
    val endDate: LocalDate,
)
