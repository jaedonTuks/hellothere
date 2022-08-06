package hellothere.config.stats

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "stats")
class StatsConfig {
    var workingStartTime: String = "08:00:00"
    var workingEndTime: String = "17:00:00"
    var cutOffHours: Long = 48

    var readConfig: StatConfigItem = StatConfigItem()
    var labelConfig: StatConfigItem = StatConfigItem()
    var replyConfig: StatConfigItem = StatConfigItem()

    class StatConfigItem {
        var xp = 10
        var penalty = 5
        var cutOffPenalty = 8
        var leniencyMinutes = 60L
    }
}
