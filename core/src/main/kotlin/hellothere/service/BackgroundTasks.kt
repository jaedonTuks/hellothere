package hellothere.service

import org.slf4j.Logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class BackgroundTasks(
    private val notificationService: NotificationService
) {

    @Scheduled(cron = "0 */15 7-18 * * *")
    fun checkForNewEmails() {
        notificationService.checkForNewEmails()
    }

    companion object {
        val LOGGER: Logger = org.slf4j.LoggerFactory.getLogger(BackgroundTasks::class.java)
    }
}
