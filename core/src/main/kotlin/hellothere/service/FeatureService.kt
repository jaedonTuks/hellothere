package hellothere.service

import hellothere.model.feature.FF4jFeature
import org.ff4j.FF4j
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
class FeatureService(
    private val ff4j: FF4j
) {

    fun isEnabled(ff4jFeature: FF4jFeature): Boolean {
        return try {
            ff4j.check(ff4jFeature.name)
        } catch (exception: Exception) {
            LOGGER.error("Fetching feature $ff4jFeature failed")
            LOGGER.debug("Failed with stack trace:", exception)
            return false
        }
    }

    fun isDisabled(ff4jFeature: FF4jFeature): Boolean {
        return try {
            !ff4j.check(ff4jFeature.name)
        } catch (exception: Exception) {
            LOGGER.error("Fetching feature $ff4jFeature failed")
            LOGGER.debug("Failed with stack trace:", exception)
            return true
        }
    }

    companion object {
        val LOGGER: Logger = org.slf4j.LoggerFactory.getLogger(FeatureService::class.java)
    }
}
