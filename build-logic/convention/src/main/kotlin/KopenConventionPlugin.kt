import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.security.Provider


class KopenConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        val enableKopenProperty = providers
            .gradleProperty("enableKopen")
            .map { it.toBoolean() }
            .orElse(false)

        val extension = extensions.create(
            "kopen",
            KopenExtension::class.java,
            objects
        )
        extension.mode.convention(KopenMode.TESTS)
        val isTestRun = isRootTestTask()
        evaluationDependsOn(":make-open")
        val makeOpenJarTask = project(":make-open").tasks.named<Jar>("jar")
        val makeOpenJarFile = makeOpenJarTask.flatMap { it.archiveFile }
        tasks.withType<KotlinCompile>().configureEach {
            dependsOn(makeOpenJarTask)
            compilerOptions.freeCompilerArgs.addAll(
                extension.mode.zip(makeOpenJarFile) { mode, jarFile ->
                    val enabled = when (mode) {
                        KopenMode.OFF -> false
                        KopenMode.ALL -> true
                        KopenMode.TESTS -> isTestRun.get()
                    } || enableKopenProperty.get()
                    if (enabled) {
                        listOf("-Xplugin=${jarFile.asFile.absolutePath}")
                    } else {
                        emptyList()
                    }
                }
            )
        }
    }

    private fun Project.isRootTestTask(): org.gradle.api.provider.Provider<Boolean> {
        val isTestRunName = providers.provider {
            val requested = gradle.startParameter.taskNames
            requested.any { name ->
                val isTest = name.contains("test", ignoreCase = true)
                val isCheck = name.contains("check", ignoreCase = true)
                isTest || isCheck
            }
        }
        val isTestRunRequest = providers.provider {
            val args = gradle.startParameter.taskRequests
                .flatMap { it.args }
                .filterNot { it.startsWith("--") }

            args.any { raw ->
                val name = raw.substringAfterLast(":")
                val isTest = name.contains("test", ignoreCase = true)
                val isCheck = name.contains("check", ignoreCase = true)
                isTest || isCheck
            }
        }
        return isTestRunRequest.zip(isTestRunName) { request, name -> request || name }
    }
}