package hellothere.dto.email

data class EmailsContainerDTO(
    val emailThreads: List<EmailThreadDto>,
    val nextPageToken: String?,
    val shouldResetEmails: Boolean
)
