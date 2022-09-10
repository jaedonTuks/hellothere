package hellothere.dto.leaderboards

data class UserLeaderBoardDTO(
    val username: String,
    val title: String,
    val totalXp: Int,
    val challengesCompleted: Int = 0
)
