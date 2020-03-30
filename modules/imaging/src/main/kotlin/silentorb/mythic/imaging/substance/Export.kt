package silentorb.mythic.imaging.substance

import silentorb.imp.execution.CompleteFunction
import silentorb.imp.execution.newLibrary
import silentorb.mythic.imaging.substance.functions.sphereFunctions
import silentorb.mythic.imaging.texturing.filters.completeTexturingAliases
import silentorb.mythic.imaging.texturing.filters.completeTexturingFunctions

fun substanceFunctions(): List<CompleteFunction> =
    sphereFunctions()

fun substanceLibrary() =
    newLibrary(substanceFunctions())
