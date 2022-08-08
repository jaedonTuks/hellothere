package hellothere.model.stats

import hellothere.model.stats.category.*
import hellothere.model.user.User
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "user_week_stats")
class WeekStats(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "start_date")
    val startDate: LocalDate,

    @Column(name = "end_date")
    val endDate: LocalDate,

    @OneToOne(cascade = [CascadeType.ALL])
    val readWeekStat: ReadWeekStat,

    @OneToOne(cascade = [CascadeType.ALL])
    val labelWeekStat: LabelWeekStat,

    @OneToOne(cascade = [CascadeType.ALL])
    val replyWeekStat: ReplyWeekStat,

    @ManyToOne
    @JoinColumn(name = "app_user")
    var user: User? = null
) {
    fun isCurrentWeek(): Boolean {
        val thisWeek = LocalDate.now()
        return thisWeek >= startDate && thisWeek <= endDate
    }

    fun addXP(increaseAmount: Int, statCategory: StatCategory): Boolean {
        if (this.isCurrentWeek()) {
            when (statCategory) {
                StatCategory.READ -> readWeekStat.addXP(increaseAmount)
                StatCategory.LABEL -> labelWeekStat.addXP(increaseAmount)
                StatCategory.REPLY -> replyWeekStat.addXP(increaseAmount)
            }
        }

        return this.isCurrentWeek()
    }

    fun getTotalExperience(): Int {
        return readWeekStat.experience + labelWeekStat.experience + replyWeekStat.experience
    }
}
