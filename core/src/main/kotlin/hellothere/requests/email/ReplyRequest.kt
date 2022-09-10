package hellothere.requests.email

import org.springframework.web.multipart.MultipartFile

data class ReplyRequest(
    val threadId: String,
    val reply: String,
    val attachments: List<MultipartFile>?
)
