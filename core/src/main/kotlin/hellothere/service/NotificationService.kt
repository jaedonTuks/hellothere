package hellothere.service

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.WebpushConfig
import com.google.firebase.messaging.WebpushNotification
import hellothere.dto.user.NotificationTokenDTO
import hellothere.model.user.User
import hellothere.repository.user.UserRepository
import hellothere.service.google.GmailService
import liquibase.pro.packaged.it
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct

@Service
class NotificationService(
    private val userRepository: UserRepository,
    private val gmailService: GmailService
) {
    @Value("\${firebase.config}")
    lateinit var firebaseConfigPath: String

    @Value("\${firebase.key}")
    lateinit var firebaseVapidKey: String

    @Value("\${firebase.pass}")
    lateinit var firebasePass: String

    @PostConstruct
    fun init() {
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(ClassPathResource(firebaseConfigPath).inputStream))
            .build()
        FirebaseApp.initializeApp(options)
    }

    @Transactional
    fun checkForNewEmails() {
        LOGGER.info("Checking for new emails")
        val users = userRepository.findAll()

        val usersWithNewEmail = users.filter { user ->
            val client = gmailService.getGmailClientFromUsername(user.id)
            client?.let { gmailService.hasNewMessageInInbox(it, user.id) } ?: false
        }

        usersWithNewEmail.forEach {
            sendNotification(it)
        }
    }

    fun sendNotification(user: User) {
        if (user.firebaseToken == null) {
            LOGGER.error("No user token to send push notification to")
            return
        }

        sendFirebasePushNotification(user.id, user.firebaseToken!!)
    }

    fun sendNotification(username: String) {
        val user = userRepository.findByIdOrNull(username)

        if (user?.firebaseToken == null) {
            LOGGER.error("No user token to send push notification to")
            return
        }

        sendFirebasePushNotification(username, user.firebaseToken!!)
    }

    private fun sendFirebasePushNotification(username: String, token: String) {
        val webPushConfig = WebpushConfig.builder()
            .setNotification(buildNewEmailNotification(username))
            .build()

        val message = Message.builder()
            .setWebpushConfig(webPushConfig)
            .setToken(token)
            .build()

        FirebaseMessaging.getInstance().send(message)
    }

    private fun buildNewEmailNotification(username: String): WebpushNotification {
        return WebpushNotification.builder()
            .setTitle("Hello there $username!")
            .setBody("You have new emails in your inbox!")
            .build()
    }

    @Transactional
    fun getUserNotificationToken(username: String): NotificationTokenDTO? {
        val user = userRepository.findByIdOrNull(username)
        if (user == null) {
            LOGGER.error("No user token found for username $username")
            return null
        }
        return NotificationTokenDTO(
            firebaseVapidKey,
            username,
            firebasePass
        )
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(NotificationService::class.java)
    }
}
