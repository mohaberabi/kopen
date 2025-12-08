import org.gradle.api.model.ObjectFactory
import javax.inject.Inject
import org.gradle.api.provider.Property


abstract class KopenExtension @Inject constructor(
    objects: ObjectFactory
) {
    val kopenEnabled: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
}