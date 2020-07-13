package silentorb.mythic.aura.generation.imp

import silentorb.imp.execution.newLibrary

fun auraLibrary() =
    newLibrary(
        functions = auraFunctions(),
        typeAliases = auraAliases()
    )
