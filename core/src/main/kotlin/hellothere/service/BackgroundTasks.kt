package hellothere.service

import org.slf4j.Logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class BackgroundTasks(
    private val notificationService: NotificationService
) {

    @Scheduled(cron = "0 0/15 9-17 * * *")
    fun checkForNewEmails() {
        // todo implement to send notification
        notificationService.checkForNewEmails()

    }

    companion object {
        val LOGGER: Logger = org.slf4j.LoggerFactory.getLogger(BackgroundTasks::class.java)
    }
}
