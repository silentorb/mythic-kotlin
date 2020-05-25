package silentorb.mythic.imaging.fathoming

import silentorb.imp.execution.newLibrary
import silentorb.mythic.imaging.fathoming.functions.fathomFunctions

fun fathomLibrary() =
    newLibrary(fathomFunctions(), fathomTypes(), fathomAliases())
