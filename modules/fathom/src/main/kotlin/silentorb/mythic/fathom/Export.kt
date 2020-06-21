package silentorb.mythic.fathom

import silentorb.imp.execution.newLibrary
import silentorb.mythic.fathom.functions.fathomFunctions
import silentorb.mythic.fathom.misc.fathomAliases
import silentorb.mythic.fathom.misc.fathomTypes

fun fathomLibrary() =
    newLibrary(fathomFunctions(), fathomTypes(), fathomAliases())
