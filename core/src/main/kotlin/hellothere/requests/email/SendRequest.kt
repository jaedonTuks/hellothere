package hellothere.requests.email

import org.springframework.web.multipart.MultipartFile

data class SendRequest(
    val to: List<String>,
    val cc: List<String>,
    val labels: List<String>,
    val subject: String,
    val body: String,
    val attachments: List<MultipartFile>?
)
