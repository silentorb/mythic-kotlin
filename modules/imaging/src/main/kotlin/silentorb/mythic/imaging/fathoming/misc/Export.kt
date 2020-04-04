package silentorb.mythic.imaging.fathoming

import silentorb.imp.execution.CompleteFunction
import silentorb.imp.execution.newLibrary
import silentorb.mythic.imaging.fathoming.functions.sphereFunctions

fun substanceFunctions(): List<CompleteFunction> =
    sphereFunctions()

fun substanceLibrary() =
    newLibrary(substanceFunctions())
