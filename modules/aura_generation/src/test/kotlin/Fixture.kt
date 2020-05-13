import silentorb.imp.execution.combineLibraries
import silentorb.imp.library.implementation.standard.standardLibrary
import silentorb.mythic.aura.generation.imp.auraLibrary

val library = combineLibraries(
    standardLibrary(),
    auraLibrary()
)

val context = listOf(library.namespace)
