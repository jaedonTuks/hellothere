package hellothere.requests.label

data class UpdateLabelViewableRequest(
    val labelId: String,
    val isViewable: Boolean
)
