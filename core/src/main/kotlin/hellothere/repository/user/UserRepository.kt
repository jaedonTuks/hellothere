package hellothere.repository.user

import hellothere.model.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun countByLeaderboardUsername(leaderboardUsername: String): Int
}
