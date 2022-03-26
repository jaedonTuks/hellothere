package hellothere.service

import hellothere.model.feature.FF4jFeature
import org.ff4j.FF4j
import org.springframework.stereotype.Service

@Service
class FeatureService(
    private val ff4j: FF4j
) {

    fun isEnabled(ff4jFeature: FF4jFeature): Boolean {
        return ff4j.check(ff4jFeature.name)
    }

    fun isDisabled(ff4jFeature: FF4jFeature): Boolean {
        return !ff4j.check(ff4jFeature.name)
    }
}
