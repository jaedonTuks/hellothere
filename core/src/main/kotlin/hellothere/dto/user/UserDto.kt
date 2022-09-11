package hellothere.dto.user

import hellothere.dto.stats.MessageTotalsSummaryDTO

// todo add badge names
// rank should change to a number placement string. With special consideration
// for first and second and third
// return xp
data class UserDto(
    val email: String,
    val leaderboardUsername: String,
    val title: String,
    val availableTitles: List<String>,
    val rank: Long,
    val currentWeekStats: WeekStatsDto?,
    val orderedWeekStats: List<WeekStatsDto> = listOf(),
    val messageTotalsSummary: MessageTotalsSummaryDTO? = null,
    val totalExperience: Int = 0,
)
