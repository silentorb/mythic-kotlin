package silentorb.mythic.glowing

fun paddedFloatBytes(byteSize: Int) =
    byteSize + (byteSize % 4)

class VertexSchema(val attributes: List<VertexAttribute>) {
  val byteSize = paddedFloatBytes(attributes.sumBy { it.byteSize })
  val floatSize = byteSize / 4
}
