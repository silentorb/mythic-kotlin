package silentorb.mythic.fathom

import silentorb.imp.execution.newLibrary
import silentorb.mythic.fathom.functions.fathomFunctions

fun fathomLibrary() =
    newLibrary(fathomFunctions(), fathomTypes(), fathomAliases())
