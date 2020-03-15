import silentorb.imp.execution.combineLibraries
import silentorb.imp.execution.newLibrary
import silentorb.imp.library.implementation.standard.standardLibrary
import silentorb.mythic.debugging.globalProfiler
import silentorb.mythic.imaging.operators.completeTexturingAliases
import silentorb.mythic.imaging.operators.completeTexturingFunctions

val library = combineLibraries(
    standardLibrary(),
    newLibrary(completeTexturingFunctions().map {
      it.copy(
          implementation = globalProfiler().wrap(it.path.name, it.implementation)
      )
    }, completeTexturingAliases())
)

val context = listOf(library.namespace)
