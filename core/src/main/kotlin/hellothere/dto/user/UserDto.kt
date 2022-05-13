package hellothere.dto.user

import hellothere.model.user.Rank

data class UserDto(
    val username: String,
    val rank: Rank
)
