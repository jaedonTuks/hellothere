package hellothere.dto.user

import hellothere.model.user.Rank

// todo add badge names
// rank should change to a number placement string. With special consideration
// for first and second and third
// return xp
data class UserDto(
    val username: String,
    val rank: Rank,
    val currentWeekStats: WeekStatsDto?,
    val totalExperience: Int = 0,
)
