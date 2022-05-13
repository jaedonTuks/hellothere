package hellothere.model.user

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
)
