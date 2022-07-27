package hellothere.repository.label

import hellothere.model.label.UserLabel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserLabelRepository : JpaRepository<UserLabel, Long> {
   fun findAllByGmailIdIn(gmailIds: List<String>): List<UserLabel>

   fun findAllByUserId(userId: String): List<UserLabel>
   fun findAllByUserIdAndNameInIgnoreCase(userId: String, names: List<String>): List<UserLabel>
}
