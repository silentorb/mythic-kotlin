package silentorb.mythic.glowing

fun paddedFloatBytes(byteSize: Int) =
    byteSize + (byteSize % 4)

class VertexSchema(inputAttributes: List<VertexAttribute>) {
  val byteSize = paddedFloatBytes(inputAttributes.sumBy { it.byteSize })
  val floatSize = byteSize / 4
  val attributes: List<VertexAttributeDetail>

  init {
    var offset = 0
    attributes = inputAttributes.mapIndexed { i, input ->
      val result = VertexAttributeDetail(
          id = i,
          name = input.name,
          offset = offset,
          count = input.count,
          byteSize = input.byteSize,
          floatSize = input.byteSize / 4,
          elementType = input.elementType
      )
      offset += input.byteSize
      result
    }
  }

  fun getAttribute(name: String) = attributes.first({ it.name == name })
}
