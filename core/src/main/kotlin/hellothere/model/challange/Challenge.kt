package hellothere.model.challange

import hellothere.model.stats.category.StatCategory
import javax.persistence.*

@Entity
@Table(name = "challenge")
class Challenge(
    @Id
    @Column(name = "id")
    val id: Long,

    @Column(name = "name")
    val name: String,

    @Column(name = "title")
    val title: String,

    @Column(name = "level")
    val level: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "stat_category")
    val statCategory: StatCategory,

    @Column(name = "description")
    val description: String,

    @Column(name = "goal")
    val goal: Int,

    // todo expand to badges as well
    @Column(name = "reward")
    val reward: Int,
)
