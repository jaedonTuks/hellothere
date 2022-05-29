package hellothere.requests.email

data class ReplyRequest(
    val threadId: String,
    val messageId: String,
    val reply: String
)
