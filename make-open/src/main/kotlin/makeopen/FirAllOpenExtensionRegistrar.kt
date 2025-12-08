package makeopen

import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclarationStatus
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.extensions.FirStatusTransformerExtension
import org.jetbrains.kotlin.fir.extensions.transform

//FIR -> FRONT END INTERMEDIATE REPRESENTATION
class FirAllOpenExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::FirAllOpenStatusTransformer
    }
}

class FirAllOpenStatusTransformer(
    session: FirSession
) : FirStatusTransformerExtension(session) {
    override fun needTransformStatus(
        declaration: FirDeclaration
    ): Boolean = when (declaration) {
        is FirRegularClass,
        is FirSimpleFunction,
        is FirProperty -> true

        else -> false
    }

    override fun transformStatus(
        status: FirDeclarationStatus,
        declaration: FirDeclaration
    ): FirDeclarationStatus = when (declaration) {
        is FirRegularClass,
        is FirSimpleFunction,
        is FirProperty ->
            status.transform(modality = Modality.OPEN)

        else -> status
    }
}