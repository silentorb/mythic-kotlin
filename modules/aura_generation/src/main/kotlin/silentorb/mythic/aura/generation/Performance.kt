package silentorb.mythic.aura.generation

enum class PlacementMethod(val value: Int) {
    prepend(0),
    append(1),
    beforeSibling(2),
    afterSibling(3),
    replace(4)
}

fun newSynthMessage(name: String, id: Int, placementMethod: PlacementMethod, placementContext: Int) =
    Message(
        Commands.newSynth, listOf(
            name,
            id,
            placementMethod.value,
            placementContext
        )
    )
