package hellothere.requests.email

data class SendRequest(
    val to: String,
    val subject: String,
    val body: String
)
