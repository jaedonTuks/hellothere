package hellothere.model.user

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "user_week_stats")
class WeekStats(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "experience")
    var experience: Int = 0,

    @Column(name = "start_date")
    val startDate: LocalDate,

    @Column(name = "end_date")
    val endDate: LocalDate,

    @ManyToOne
    @JoinColumn(name = "app_user")
    var user: User? = null

) {
    fun isCurrentWeek(): Boolean {
        val thisWeek = LocalDate.now()
        return thisWeek >= startDate && thisWeek <= endDate
    }

    fun addXP(increaseAmount: Int): Boolean {
        if (this.isCurrentWeek()) {
            experience += increaseAmount
        }

        return this.isCurrentWeek()
    }
}
