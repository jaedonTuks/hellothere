package hellothere.service

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.WebpushConfig
import com.google.firebase.messaging.WebpushNotification
import hellothere.dto.user.NotificationTokenDTO
import hellothere.repository.user.UserRepository
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
    private val userRepository: UserRepository
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

    fun sendNotification(username: String) {
        val user = userRepository.findByIdOrNull(username)

        if (user?.firebaseToken == null) {
            LOGGER.error("No user token to send push notification to")
            return
        }

        val webPushConfig = WebpushConfig.builder()
            .setNotification(buildNewEmailNotification(username))
            .build()

        val message = Message.builder()
            .setWebpushConfig(webPushConfig)
            .setToken(user.firebaseToken)
            .build()

        FirebaseMessaging.getInstance().send(message)
    }

    private fun buildNewEmailNotification(username: String): WebpushNotification {
        return WebpushNotification.builder()
            .setTitle(username)
            .setBody("You have new emails in your inbox! Open hello there to check them out")
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
