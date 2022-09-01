package hellothere.dto.user

data class NotificationTokenDTO(
    val vapidKey: String,
    val username: String,
    val pass: String
)
