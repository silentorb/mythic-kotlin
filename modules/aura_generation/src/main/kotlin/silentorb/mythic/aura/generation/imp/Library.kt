package silentorb.mythic.aura.generation.imp

import silentorb.imp.execution.Library
import silentorb.imp.execution.newLibrary

fun auraLibrary(): Library =
    newLibrary(
        functions = auraFunctions(),
        typeAliases = auraAliases()
    )
