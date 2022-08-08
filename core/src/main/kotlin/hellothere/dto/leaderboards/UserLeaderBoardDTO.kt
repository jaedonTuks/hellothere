package hellothere.dto.leaderboards

data class UserLeaderBoardDTO(
    val username: String,
    val totalXp: Int,
    val challengesCompleted: Int = 0
)
