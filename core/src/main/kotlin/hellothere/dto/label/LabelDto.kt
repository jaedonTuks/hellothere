package hellothere.dto.label

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class LabelDto(
    val id: String,
    @JsonIgnore val name: String,
    val unreadThreads: Int = 0,
    val color: String = "#FFF",
    val isManageable: Boolean
) {
    @JsonProperty("name")
    fun getFormattedName(): String {
        return this.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
}
