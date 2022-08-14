package hellothere.service.google.BatchCallbacks

import com.google.api.client.googleapis.batch.json.JsonBatchCallback
import com.google.api.client.googleapis.json.GoogleJsonError
import com.google.api.client.http.HttpHeaders
import com.google.api.services.gmail.model.Label
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LabelBatchCallback(
    private val labelList: MutableList<Label>
) : JsonBatchCallback<Label>() {
    override fun onSuccess(label: Label?, headers: HttpHeaders?) {
        if (label == null) {
            LOGGER.info("No label found for headers $headers")
        } else {
            labelList.add(label)
        }
    }

    override fun onFailure(error: GoogleJsonError?, headers: HttpHeaders?) {
        LOGGER.error("Unable to fetch label with headers $headers encountered error $error")
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(LabelBatchCallback::class.java)
    }
}
