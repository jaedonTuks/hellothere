package hellothere.annotation

import hellothere.model.feature.FF4jFeature

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class RequiresFeature(
    val value: FF4jFeature
)
