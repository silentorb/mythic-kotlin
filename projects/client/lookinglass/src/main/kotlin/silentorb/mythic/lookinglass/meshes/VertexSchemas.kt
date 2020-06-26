package silentorb.mythic.lookinglass.meshes

import org.lwjgl.opengl.GL11.GL_BYTE
import org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE
import silentorb.mythic.drawing.DrawingVertexSchemas
import silentorb.mythic.drawing.createDrawingVertexSchemas
import silentorb.mythic.glowing.VertexAttribute
import silentorb.mythic.glowing.VertexSchema
import silentorb.mythic.glowing.floatVertexAttribute

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
        floatVertexAttribute(AttributeName.position, 3),
        floatVertexAttribute(AttributeName.uv, 2)
    )),
    shaded = VertexSchema(listOf(
        floatVertexAttribute(AttributeName.position, 3),
        floatVertexAttribute(AttributeName.normal, 3)
    )),
    shadedPoint = VertexSchema(listOf(
        floatVertexAttribute(AttributeName.position, 3),
        VertexAttribute(AttributeName.color, 4, GL_UNSIGNED_BYTE, true),
        floatVertexAttribute(AttributeName.pointSize, 1),
        VertexAttribute(AttributeName.normal, 3, GL_BYTE, true),
        VertexAttribute(AttributeName.level, 1, GL_BYTE, false)
    )),
    textured = VertexSchema(listOf(
        floatVertexAttribute(AttributeName.position, 3),
        floatVertexAttribute(AttributeName.normal, 3),
        floatVertexAttribute(AttributeName.uv, 2)
    )),
    animated = VertexSchema(listOf(
        floatVertexAttribute(AttributeName.position, 3),
        floatVertexAttribute(AttributeName.normal, 3),
        floatVertexAttribute(AttributeName.uv, 2),
        floatVertexAttribute(AttributeName.joints, 4),
        floatVertexAttribute(AttributeName.weights, 4)
    )),
    flat = VertexSchema(listOf(
        floatVertexAttribute(AttributeName.position, 3)
    )),
    imported = VertexSchema(listOf(
        floatVertexAttribute(AttributeName.position, 3),
        floatVertexAttribute(AttributeName.normal, 3)
    )),
    drawing = createDrawingVertexSchemas()
)
