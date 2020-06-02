import silentorb.imp.execution.combineLibraries
import silentorb.imp.execution.newLibrary
import silentorb.imp.library.standard.standardLibrary
import silentorb.mythic.imaging.texturing.filters.completeTexturingAliases
import silentorb.mythic.imaging.texturing.filters.completeTexturingFunctions

val library = combineLibraries(
    standardLibrary(),
    newLibrary(completeTexturingFunctions().map {
      it.copy(
//          implementation = globalProfiler().wrap(it.path.name, it.implementation)
      )
    }, typeAliases = completeTexturingAliases())
)

val context = listOf(library.namespace)
