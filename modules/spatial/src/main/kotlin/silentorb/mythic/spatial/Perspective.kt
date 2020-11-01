package silentorb.mythic.spatial

fun createViewMatrix(position: Vector3, orientation: Quaternion): Matrix {
  return Matrix.identity
      .rotateZ(Pi / 2f)
      .rotateY(Pi / 2f)
      .rotate(-orientation)
      .translate(-position)
}

fun getAspectRatio(dimensions: Vector2i): Float {
  return dimensions.x.toFloat() / dimensions.y.toFloat()
}

fun createPerspectiveMatrix(dimensions: Vector2i, angle: Float, nearClip: Float, farClip: Float): Matrix {
  val ratio = getAspectRatio(dimensions)
  val radians = Math.toRadians(angle.toDouble()).toFloat()
  return toMatrix(toMutableMatrix(zeroMatrix())
      .setPerspective(radians, ratio, nearClip, farClip))
}

fun createOrthographicMatrix(dimensions: Vector2i, zoom: Float, nearClip: Float, farClip: Float): Matrix {
  val ratio = getAspectRatio(dimensions)
  return toMatrix(toMutableMatrix(Matrix.identity)
      .setOrtho(-1f * zoom, 1f * zoom, -1f * zoom, 1f * zoom, nearClip, farClip))
}
