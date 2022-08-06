package hellothere.model.stats.category

import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "stat_type")
open class WeekStatsCategory(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    open val id: Long? = null,

    @Column(name = "experience")
    open var experience: Int = 0
) {
    fun addXP(xpToAdd: Int) {
        experience += xpToAdd
    }
}
