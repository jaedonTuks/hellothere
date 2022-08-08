package hellothere.repository.user

import hellothere.model.stats.WeekStats
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

    @Query(
        value = "select ws, (ws.labelWeekStat.experience + ws.readWeekStat.experience + ws.replyWeekStat.experience) as totalXP from WeekStats ws where ws.startDate <= :date and ws.endDate >= :date order by totalXP desc",
    )
    fun findAllByDateBetween(
        @Param("date") date: LocalDate
    ): List<WeekStats>

    @Query(
        value = "select count(ws) from WeekStats ws where ws.startDate <= :date and ws.endDate >= :date and ((ws.labelWeekStat.experience + ws.readWeekStat.experience + ws.replyWeekStat.experience) > :currentXP)",
    )
    fun findCountWithWithXpAbove(
        @Param("currentXP") currentXP: Int,
        @Param("date") date: LocalDate
    ): Long

    fun findAllByUserId(userId: String): List<WeekStats>
}
