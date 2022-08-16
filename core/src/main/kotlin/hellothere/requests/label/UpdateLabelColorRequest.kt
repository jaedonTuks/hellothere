package hellothere.requests.label

data class UpdateLabelColorRequest(
    val labelId: String,
    val color: String
)
