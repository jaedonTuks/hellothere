package hellothere.model.stats.category

import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue("READ")
class ReadWeekStat(
    id: Long? = null,
    experience: Int = 0
) : WeekStatsCategory(id, experience)
