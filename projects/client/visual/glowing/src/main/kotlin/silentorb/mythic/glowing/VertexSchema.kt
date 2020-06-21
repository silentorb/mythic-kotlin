package silentorb.mythic.glowing

fun byteSizeToPaddedFloatSize(byteSize: Int) =
    byteSize + (byteSize % 4) / 4

class VertexSchema(inputAttributes: List<VertexAttribute>) {
  val floatSize = byteSizeToPaddedFloatSize(inputAttributes.sumBy { it.byteSize })
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
