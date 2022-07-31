package hellothere.repository.user

import hellothere.model.user.WeekStats
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface WeekStatsRepository : JpaRepository<WeekStats, Long> {

    @Query("select ws from WeekStats ws where ws.user.id = :userId and ws.startDate <= :date and ws.endDate >= :date")
    fun findFirstByUserIdAndDateBetween(
        @Param("userId") userId: String,
        @Param("date") date: LocalDate
    ): WeekStats?

    fun findAllByUserId(userId: String): List<WeekStats>
}
