package hellothere.dto.leaderboards

data class TopThreeDto(
    val first: UserLeaderBoardDTO,
    val second: UserLeaderBoardDTO,
    val third: UserLeaderBoardDTO,
)
