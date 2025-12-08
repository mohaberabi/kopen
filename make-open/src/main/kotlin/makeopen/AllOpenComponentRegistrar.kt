package makeopen

import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

@OptIn(ExperimentalCompilerApi::class)
class AllOpenComponentRegistrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean
        get() = true

    @OptIn(ExperimentalCompilerApi::class)
    override fun ExtensionStorage.registerExtensions(
        configuration: CompilerConfiguration
    ) {

        FirExtensionRegistrarAdapter.registerExtension(FirAllOpenExtensionRegistrar())
    }
}