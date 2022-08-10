package hellothere.requests.challenge

data class ClaimChallengeRewardRequest(
    val username: String,
    val challengeId: Long
)
