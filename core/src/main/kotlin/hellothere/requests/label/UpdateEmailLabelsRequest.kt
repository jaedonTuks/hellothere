package hellothere.requests.label

data class UpdateEmailLabelsRequest(
    val threadIds: List<String>,
    val addLabels: List<String>,
    val removeLabels: List<String>,
)
