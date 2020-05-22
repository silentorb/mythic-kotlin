package silentorb.mythic.lookinglass.meshes

import silentorb.mythic.drawing.DrawingVertexSchemas
import silentorb.mythic.drawing.createDrawingVertexSchemas
import silentorb.mythic.glowing.VertexAttribute

typealias VertexSchema = silentorb.mythic.glowing.VertexSchema

data class VertexSchemas(
    val billboard: VertexSchema,
    val imported: VertexSchema,
    val textured: VertexSchema,
    val flat: VertexSchema,
    val animated: VertexSchema,
    val shaded: VertexSchema,
    val shadedPoint: VertexSchema,
    val drawing: DrawingVertexSchemas
)

fun createVertexSchemas() = VertexSchemas(
    billboard = VertexSchema(listOf(
        VertexAttribute(AttributeName.position, 3),
        VertexAttribute(AttributeName.uv, 2)
    )),
    shaded = VertexSchema(listOf(
        VertexAttribute(AttributeName.position, 3),
        VertexAttribute(AttributeName.normal, 3)
    )),
    shadedPoint = VertexSchema(listOf(
        VertexAttribute(AttributeName.position, 3),
        VertexAttribute(AttributeName.normal, 3),
        VertexAttribute(AttributeName.pointSize, 1),
        VertexAttribute(AttributeName.color, 4)
    )),
    textured = VertexSchema(listOf(
        VertexAttribute(AttributeName.position, 3),
        VertexAttribute(AttributeName.normal, 3),
        VertexAttribute(AttributeName.uv, 2)
    )),
    animated = VertexSchema(listOf(
        VertexAttribute(AttributeName.position, 3),
        VertexAttribute(AttributeName.normal, 3),
        VertexAttribute(AttributeName.uv, 2),
        VertexAttribute(AttributeName.joints, 4),
        VertexAttribute(AttributeName.weights, 4)
    )),
    flat = VertexSchema(listOf(
        VertexAttribute(AttributeName.position, 3)
    )),
    imported = VertexSchema(listOf(
        VertexAttribute(AttributeName.position, 3),
        VertexAttribute(AttributeName.normal, 3)
    )),
    drawing = createDrawingVertexSchemas()
)
