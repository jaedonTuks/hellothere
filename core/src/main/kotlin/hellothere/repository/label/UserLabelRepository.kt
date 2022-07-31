package hellothere.repository.label

import hellothere.model.label.UserLabel
import hellothere.model.label.UserLabelId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserLabelRepository : JpaRepository<UserLabel, UserLabelId> {
    fun findAllByIdIn(ids: List<UserLabelId>): List<UserLabel>
    fun findAllByUserId(userId: String): List<UserLabel>
    fun findAllByUserIdAndNameInIgnoreCase(userId: String, names: List<String>): List<UserLabel>
}
