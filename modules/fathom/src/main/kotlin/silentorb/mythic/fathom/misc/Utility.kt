package silentorb.mythic.fathom.misc

import silentorb.mythic.scenery.Shading
import silentorb.mythic.spatial.Vector3

fun newShading(color: Vector3) =
    Shading(
        color = color,
        opacity = 1f,
        specular = 0.8f,
        glow = 0f
    )
