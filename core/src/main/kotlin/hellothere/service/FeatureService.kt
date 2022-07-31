package hellothere.service

import hellothere.model.feature.FF4jFeature
import hellothere.model.feature.FF4jProperty
import org.ff4j.FF4j
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
class FeatureService(
    val ff4j: FF4j
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

    final inline fun <reified T> getProperty(fF4jProperty: FF4jProperty) : T {
        val property = ff4j.getProperty(fF4jProperty.toString()).value.toString()

        return when (T::class) {
            Int::class -> property.toInt() as T
            String::class -> property as T
            else -> throw IllegalStateException("Unknown Generic Type")
        }
    }

    companion object {
        val LOGGER: Logger = org.slf4j.LoggerFactory.getLogger(FeatureService::class.java)
    }
}
