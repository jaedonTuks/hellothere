package hellothere.annotation

import hellothere.service.FeatureService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Aspect
@Component
class RequiresFeatureAspect @Autowired constructor(private val featureService: FeatureService) {

    @Around("@annotation(feature)")
    fun requiresFeature(joinPoint: ProceedingJoinPoint, feature: RequiresFeature): Any? {
        if (featureService.isEnabled(feature.value)) {
            return joinPoint.proceed()
        }

        LOGGER.info("Feature ${feature.value} has been disabled")
        return null
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(RequiresFeatureAspect::class.java)
    }
}
