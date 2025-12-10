import org.gradle.api.model.ObjectFactory
import javax.inject.Inject
import org.gradle.api.provider.Property

enum class KopenMode { OFF, TESTS, ALL }


abstract class KopenExtension @Inject constructor(
    objects: ObjectFactory
) {
    val mode: Property<KopenMode> = objects.property(KopenMode::class.java)
}