package hellothere.service.google.BatchCallbacks

import com.google.api.client.googleapis.batch.json.JsonBatchCallback
import com.google.api.client.googleapis.json.GoogleJsonError
import com.google.api.client.http.HttpHeaders
import com.google.api.services.gmail.model.Thread
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ThreadBatchCallback(
    private val threadList: MutableList<Thread>
) : JsonBatchCallback<Thread>() {
    override fun onSuccess(thread: Thread?, headers: HttpHeaders?) {
        if (thread == null) {
            LOGGER.info("No label found for headers $headers")
        } else {
            threadList.add(thread)
        }
    }

    override fun onFailure(error: GoogleJsonError?, headers: HttpHeaders?) {
        LOGGER.error("Unable to fetch thread with headers $headers encountered error $error")
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(ThreadBatchCallback::class.java)
    }
}
