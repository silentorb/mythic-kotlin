import silentorb.imp.execution.combineLibraries
import silentorb.imp.library.implementation.standard.standardLibrary
import silentorb.mythic.imaging.texturingLibrary

val library = combineLibraries(
    standardLibrary(),
    texturingLibrary()
)

val context = listOf(library.namespace)
