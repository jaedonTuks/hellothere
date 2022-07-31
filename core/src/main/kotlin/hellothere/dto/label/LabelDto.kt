package hellothere.dto.label

data class LabelDto(
    val name: String,
    val unreadThreads: Int = 0
)
