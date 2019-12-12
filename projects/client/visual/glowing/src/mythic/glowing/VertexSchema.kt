package mythic.glowing

class VertexSchema(inputAttributes: List<VertexAttribute>) {
  val floatSize = inputAttributes.sumBy { it.size }
  val attributes: List<VertexAttributeDetail>

  init {
    var offset = 0
    attributes = inputAttributes.mapIndexed { i, input ->
      val result = VertexAttributeDetail(i, input.name, offset, input.size)
      offset += input.size
      result
    }
  }

  fun getAttribute(name: String) = attributes.first({ it.name == name })
}
