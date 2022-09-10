package hellothere.dto.stats

data class MessageTotalsSummaryDTO(
    val totalEmailThreads: Int = 0,
    val totalEmails: Int = 0,
    val totalRead: Int = 0,
    val totalLabeled: Int = 0,
    val totalReplied: Int = 0
)
