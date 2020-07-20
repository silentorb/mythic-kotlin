package silentorb.mythic.fathom

import silentorb.imp.execution.newLibrary
import silentorb.mythic.fathom.functions.fathomFunctions
import silentorb.mythic.fathom.misc.fathomAliases
import silentorb.mythic.fathom.misc.fathomTypes
import silentorb.mythic.fathom.spatial.spatialFunctions

fun fathomLibrary() =
    newLibrary(
        functions = fathomFunctions() +distanceFunctions() + spatialFunctions(),
        typeNames = fathomTypes(),
        typeAliases = fathomAliases()
    )
