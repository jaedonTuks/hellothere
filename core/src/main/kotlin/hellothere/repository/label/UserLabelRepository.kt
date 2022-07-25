package hellothere.repository.label

import hellothere.model.label.UserLabel
import hellothere.model.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserLabelRepository : JpaRepository<UserLabel, Long> {
   fun findAllByGmailIdIn(gmailId: List<String>): List<UserLabel>
}
