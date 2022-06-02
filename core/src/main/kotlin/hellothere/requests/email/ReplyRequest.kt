package hellothere.requests.email

data class ReplyRequest(
    val threadId: String,
    val reply: String
)
