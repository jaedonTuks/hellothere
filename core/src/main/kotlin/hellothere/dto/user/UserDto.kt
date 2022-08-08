package hellothere.dto.user

import hellothere.dto.stats.MessageTotalsSummaryDTO
import hellothere.model.stats.WeekStats
import hellothere.model.user.Rank

// todo add badge names
// rank should change to a number placement string. With special consideration
// for first and second and third
// return xp
data class UserDto(
    val email: String,
    val leaderboardUsername: String,
    val rank: Long,
    val currentWeekStats: WeekStatsDto?,
    val orderedWeekStats: List<WeekStatsDto> = listOf(),
    val messageTotalsSummary: MessageTotalsSummaryDTO? = null,
    val totalExperience: Int = 0,
)
