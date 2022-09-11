package hellothere.model.user

import hellothere.config.JPA.BATCH_SIZE
import hellothere.model.stats.WeekStats
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import javax.persistence.*

@Entity
@Table(name = "app_user")
class User(
    @Id
    @Column(name = "username")
    val id: String,

    @Column(name = "leaderboard_username")
    var leaderboardUsername: String,

    @Column(name = "title")
    var title: String,

    @Column(name = "firebase_token")
    var firebaseToken: String?,

    @OneToMany(
        mappedBy = "user",
        fetch = FetchType.LAZY
    )
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = BATCH_SIZE)
    @OrderBy("endDate DESC")
    val weeklyStats: MutableList<WeekStats> = mutableListOf()
) {

    fun addWeekStats(weekStats: WeekStats) {
        weeklyStats.add(weekStats)
    }

    fun getTotalExperience(): Int {
        return weeklyStats.sumOf { it.getTotalExperience() }
    }

    fun getCurrentWeeksStats(): WeekStats? {
        val latestWeek = weeklyStats.firstOrNull()

        return if (latestWeek != null && latestWeek.isCurrentWeek()) {
            latestWeek
        } else {
            null
        }
    }
}
