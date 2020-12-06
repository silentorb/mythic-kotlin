package silentorb.mythic.bloom

import silentorb.mythic.spatial.Vector2i

typealias Plane = (Vector2i) -> Vector2i

val horizontalPlane: Plane = { it }
val verticalPlane: Plane = { Vector2i(it.y, it.x) }
