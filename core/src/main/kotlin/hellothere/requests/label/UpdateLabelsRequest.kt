package hellothere.requests.label

data class UpdateLabelsRequest(
    val threadIds: List<String>,
    val addLabels: List<String>,
    val removeLabels: List<String>,
)
