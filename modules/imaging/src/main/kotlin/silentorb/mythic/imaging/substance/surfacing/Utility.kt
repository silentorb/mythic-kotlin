package silentorb.mythic.imaging.substance.surfacing

import silentorb.mythic.spatial.Vector3

fun isInsideBounds(bounds: DecimalBounds, position: Vector3) =
    position.x >= bounds.start.x &&
        position.y >= bounds.start.y &&
        position.z >= bounds.start.z &&
        position.x < bounds.end.x &&
        position.y < bounds.end.y &&
        position.z < bounds.end.z
