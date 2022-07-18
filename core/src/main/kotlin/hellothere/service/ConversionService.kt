package hellothere.service

import com.google.api.services.gmail.model.MessagePart
import hellothere.model.email.MessagePartMimeType
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.util.*
import javax.mail.internet.MimeMessage

@Service
class ConversionService {

    fun convertMessagePartToString(messagePart: MessagePart): String {
        var returnString = ""
        if (messagePart.mimeType == MessagePartMimeType.HTML.value) {
            returnString += String(messagePart.body.decodeData(), Charset.defaultCharset())
        }

        if (messagePart.parts != null && messagePart.parts.isNotEmpty()) {
            messagePart.parts.forEach {
                returnString += convertMessagePartToString(it)
            }
        }

        return returnString
    }

    fun convertMimeMessageToBase64String(mimeMessage: MimeMessage): String? {
        return try {
            val buffer = ByteArrayOutputStream()
            mimeMessage.writeTo(buffer)
            val messageBytes = buffer.toByteArray()
            val encoder = Base64.getUrlEncoder()
            encoder.encodeToString(messageBytes)
        } catch (e: Exception) {
            LOGGER.error("Cant convert mime message to base 64 string. Encountered exception with message: ${e.message}")
            LOGGER.debug(e.stackTraceToString())
            null
        }
    }

    fun convertBase64ToReadableText(base64String: String): String? {
        return try {
            val decoder = Base64.getDecoder()
            val decodedByteArray = decoder.decode(base64String)
            String(decodedByteArray, Charset.defaultCharset())
        } catch (e: Exception) {
            LOGGER.error("Unable ro build string")
            LOGGER.error(e.stackTraceToString())
            return null
        }
    }

    fun getHtmlBody(fullBody: String): String {
        val bodyOfHtmlDoc = StringUtils.substringBetween(fullBody, "<body", "</body>")

        return if (bodyOfHtmlDoc == null) {
            fullBody
        } else {
            "<div $bodyOfHtmlDoc </div>"
        }
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(ConversionService::class.java)
    }
}
