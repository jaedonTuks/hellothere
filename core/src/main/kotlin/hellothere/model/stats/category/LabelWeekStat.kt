package hellothere.model.stats.category

import javax.persistence.DiscriminatorValue
import javax.persistence.Entity

@Entity
@DiscriminatorValue("LABEL")
class LabelWeekStat(
    id: Long? = null,
    experience: Int = 0
) : WeekStatsCategory(id, experience)
