package hellothere.dto.label

data class LabelUpdateDto(
    val threadLabelMap: Map<String, Set<String>>,
    val allLabels: List<LabelDto>
)
