import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class KopenConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val extension = extensions.create(
            "kopen",
            KopenExtension::class.java,
            objects
        )

        val enableKopenProperty = providers
            .gradleProperty("enableKopen")
            .map { it.toBoolean() }
            .orElse(false)

        extension.kopenEnabled.convention(enableKopenProperty)
        evaluationDependsOn(":make-open")
        val makeOpenJarTask = project(":make-open").tasks.named<Jar>("jar")
        val makeOpenJarFile = makeOpenJarTask.flatMap { it.archiveFile }
        tasks.withType<KotlinCompile>().configureEach {
            dependsOn(makeOpenJarTask)
            compilerOptions.freeCompilerArgs.addAll(
                extension.kopenEnabled.zip(makeOpenJarFile) { enabled, jarFile ->
                    if (enabled) {
                        listOf("-Xplugin=${jarFile.asFile.absolutePath}")
                    } else {
                        emptyList()
                    }
                }
            )
        }
    }
}