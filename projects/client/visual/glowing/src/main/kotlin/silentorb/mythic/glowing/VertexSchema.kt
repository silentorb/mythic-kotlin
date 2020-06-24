package silentorb.mythic.glowing

fun paddedFloatBytes(byteSize: Int) =
    if (byteSize % 4 == 0)
      byteSize
    else
      byteSize + 4 - (byteSize % 4)

class VertexSchema(val attributes: List<VertexAttribute>) {
  val byteSize = paddedFloatBytes(attributes.sumBy { it.byteSize })
  val floatSize = byteSize / 4
}
