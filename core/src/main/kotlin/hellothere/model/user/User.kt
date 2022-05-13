package hellothere.model.user

import hellothere.config.JPA.BATCH_SIZE
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

    @Column(name = "rank")
    @Enumerated(EnumType.STRING)
    val rank: Rank = Rank.NOOB,

    @Column(name = "total_experience")
    val totalExperience: Int = 0,

    @OneToMany(
        mappedBy = "user",
        fetch = FetchType.LAZY,
    )
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = BATCH_SIZE)
    @OrderBy("endDate DESC")
    val weeklyStats: List<WeekStats> = listOf()
) {
    fun getCurrentWeeksStats(): WeekStats? {
        val latestWeek = weeklyStats.firstOrNull()

        return if (latestWeek != null && latestWeek.isCurrentWeek()) {
            latestWeek
        } else {
            null
        }
    }
}
