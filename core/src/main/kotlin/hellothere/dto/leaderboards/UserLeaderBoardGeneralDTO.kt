package hellothere.dto.leaderboards

data class UserLeaderBoardGeneralDTO(
    val rank: Int,
    val title: String,
    val username: String,
    val totalXp: Int
)
