package mythic.drawing

import mythic.spatial.Vector4

fun grayTone(value: Float, alpha: Float) = Vector4(value, value, value, alpha)
fun grayTone(value: Float) = Vector4(value, value, value, 1f)
