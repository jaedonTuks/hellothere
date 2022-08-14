package hellothere.service.google.BatchCallbacks

import com.google.api.client.googleapis.batch.json.JsonBatchCallback
import com.google.api.client.googleapis.json.GoogleJsonError
import com.google.api.client.http.HttpHeaders
import com.google.api.services.gmail.model.Label
import com.google.api.services.gmail.model.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MessageBatchCallback(
    private val messageList: MutableList<Message>
) : JsonBatchCallback<Message>() {
    override fun onSuccess(message: Message?, headers: HttpHeaders?) {
        if (message == null) {
            LOGGER.info("No label found for headers $headers")
        } else {
            messageList.add(message)
        }
    }

    override fun onFailure(error: GoogleJsonError?, headers: HttpHeaders?) {
        LOGGER.error("Unable to fetch message with headers $headers encountered error $error")
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(MessageBatchCallback::class.java)
    }
}
