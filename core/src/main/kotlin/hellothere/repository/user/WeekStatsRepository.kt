package hellothere.repository.user

import hellothere.model.user.WeekStats
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WeekStatsRepository : JpaRepository<WeekStats, Long>
