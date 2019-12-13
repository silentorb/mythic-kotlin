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
    val drawing: DrawingVertexSchemas
)

fun createVertexSchemas() = VertexSchemas(
    billboard = VertexSchema(listOf(
        VertexAttribute(AttributeName.position.name, 3),
        VertexAttribute(AttributeName.uv.name, 2)
    )),
    shaded = VertexSchema(listOf(
        VertexAttribute(AttributeName.position.name, 3),
        VertexAttribute(AttributeName.normal.name, 3)
    )),
    textured = VertexSchema(listOf(
        VertexAttribute(AttributeName.position.name, 3),
        VertexAttribute(AttributeName.normal.name, 3),
        VertexAttribute(AttributeName.uv.name, 2)
    )),
    animated = VertexSchema(listOf(
        VertexAttribute(AttributeName.position.name, 3),
        VertexAttribute(AttributeName.normal.name, 3),
        VertexAttribute(AttributeName.uv.name, 2),
        VertexAttribute(AttributeName.joints.name, 4),
        VertexAttribute(AttributeName.weights.name, 4)
    )),
    flat = VertexSchema(listOf(
        VertexAttribute(AttributeName.position.name, 3)
    )),
    imported = VertexSchema(listOf(
        VertexAttribute(AttributeName.position.name, 3),
        VertexAttribute(AttributeName.normal.name, 3)
    )),
    drawing = createDrawingVertexSchemas()
)
