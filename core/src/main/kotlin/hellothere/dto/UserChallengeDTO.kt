package hellothere.dto

import hellothere.model.stats.category.StatCategory

data class UserChallengeDTO(
    val challengeId: Long,
    val name: String,
    val title: String,
    val color: String?,
    val goal: Int,
    val progress: Int,
    val reward: Int,
    val description: String,
    val statCategory: StatCategory,
    val isRewardClaimed: Boolean
)
