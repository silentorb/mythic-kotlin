package mythic.drawing

import mythic.glowing.*
import mythic.spatial.*
import mythic.typography.*
import org.joml.Vector2i
import org.joml.Vector4i
import kotlin.math.cos
import kotlin.math.sin

data class Meshes(
    val square: Drawable,
    val image: Drawable,
    val circle: Drawable,
    val solidCircle: Drawable
)

data class DrawingVertexSchemas(
    val simple: VertexSchema,
    val image: VertexSchema
)

fun createDrawingVertexSchemas() = DrawingVertexSchemas(
    VertexSchema(listOf(VertexAttribute("position", 2))),
    VertexSchema(listOf(VertexAttribute("vertex", 4)))
)

fun createSquareMesh(vertexSchema: VertexSchema) =
    SimpleMesh(vertexSchema, listOf(
        0f, 1f,
        0f, 0f,
        1f, 0f,
        1f, 1f
    ))

fun createImageMesh(vertexSchema: VertexSchema) =
    SimpleMesh(vertexSchema, listOf(
        0f, 1f, 0f, 1f,
        0f, 0f, 0f, 0f,
        1f, 0f, 1f, 0f,
        1f, 1f, 1f, 1f
    ))

fun createCircleList(radius: Float, count: Int): ArrayList<Float> {
  val vertices = ArrayList<Float>((count) * 2)
  val increment = Pi * 2 / count

  for (i in 0..count) {
    val theta = increment * i
    vertices.add(sin(theta) * radius)
    vertices.add(cos(theta) * radius)
  }
  return vertices
}

fun createCircleMesh(vertexSchema: VertexSchema, radius: Float, count: Int) =
    SimpleMesh(vertexSchema, createCircleList(radius, count))

fun createSolidCircleMesh(vertexSchema: VertexSchema, radius: Float, count: Int) =
    SimpleMesh(vertexSchema, listOf(0f, 0f).plus(createCircleList(radius, count)))

private val circleResolution = 32

fun createDrawingMeshes(vertexSchemas: DrawingVertexSchemas) = Meshes(
    square = createSquareMesh(vertexSchemas.simple),
    image = createImageMesh(vertexSchemas.image),
    circle = createCircleMesh(vertexSchemas.simple, 1f, circleResolution),
    solidCircle = createSolidCircleMesh(vertexSchemas.simple, 1f, circleResolution)
)

enum class FillType {
  solid,
  outline
}

data class CanvasDependencies(
    val vertexSchemas: DrawingVertexSchemas,
    val meshes: Meshes,
    val dynamicMesh: MutableSimpleMesh,
    val dynamicTexturedMesh: MutableSimpleMesh
)

private var staticCanvasDependencies: CanvasDependencies? = null

fun getStaticCanvasDependencies(): CanvasDependencies {
  if (staticCanvasDependencies == null) {
    val vertexSchemas = createDrawingVertexSchemas()
    staticCanvasDependencies = CanvasDependencies(
        vertexSchemas = vertexSchemas,
        meshes = createDrawingMeshes(vertexSchemas),
        dynamicMesh = MutableSimpleMesh(vertexSchemas.simple),
        dynamicTexturedMesh = MutableSimpleMesh(vertexSchemas.image)
    )
  }
  return staticCanvasDependencies!!
}

typealias Brush = (Matrix, Drawable) -> Unit

private var _globalFonts: List<FontSet>? = null

fun globalFonts(): List<FontSet> = _globalFonts!!

fun setGlobalFonts(fonts: List<FontSet>) {
  _globalFonts = fonts
}

class Canvas(
    val effects: DrawingEffects,
    val unitScaling: Vector2,
    val fonts: List<FontSet>,
    dimensions: Vector2i,
    dependencies: CanvasDependencies = getStaticCanvasDependencies()
) {
  val vertexSchemas = dependencies.vertexSchemas
  val meshes = dependencies.meshes

  val dynamicMesh = dependencies.dynamicMesh
  val dynamicTexturedMesh = dependencies.dynamicTexturedMesh
  val viewportDimensions = Vector2(dimensions.x.toFloat(), dimensions.y.toFloat())
  val pixelsToScalar = Matrix().scale(1f / dimensions.x, 1f / dimensions.y, 1f)

  fun transformScalar(position: Vector2, dimensions: Vector2) =
      Matrix()
          .mul(pixelsToScalar)
          .translate(position.x, position.y, 0f)
          .scale(dimensions.x, dimensions.y, 1f)

  fun drawSquare(position: Vector2, dimensions: Vector2, brush: Brush) {
    brush(transformScalar(position, dimensions), meshes.square)
  }

  fun drawCircle(position: Vector2, radius: Float, brush: Brush) {
    brush(transformScalar(position, Vector2(radius, radius)), meshes.circle)
  }

  fun drawSolidCircle(position: Vector2, radius: Float, brush: Brush) {
    brush(transformScalar(position, Vector2(radius, radius)), meshes.solidCircle)
  }

  fun draw(color: Vector4, drawMethod: DrawMethod, transform: Matrix, mesh: Drawable) {
    effects.singleColorShader.activate(transform, color)
    mesh.draw(drawMethod)
  }

  fun outline(color: Vector4, thickness: Float): Brush = { transform: Matrix, mesh: Drawable ->
    globalState.lineThickness = thickness
    draw(color, DrawMethod.lineLoop, transform, mesh)
  }

  fun solid(color: Vector4) = { transform: Matrix, mesh: Drawable ->
    draw(color, DrawMethod.triangleFan, transform, mesh)
  }

  fun image(texture: Texture) = { transform: Matrix, mesh: Drawable ->
    effects.image.activate(transform, texture)
    mesh.draw(DrawMethod.triangleFan)
  }

  fun drawImage(position: Vector2, dimensions: Vector2, brush: Brush) {
    brush(transformScalar(position, dimensions), meshes.image)
  }

  fun drawDynamicImage(position: Vector2, dimensions: Vector2, brush: Brush, vertexData: List<Float>) {
    dynamicTexturedMesh.load(vertexData)
    brush(transformScalar(position, dimensions), dynamicTexturedMesh)
  }

  fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, color: Vector4, thickness: Float) {
    dynamicMesh.load(listOf(startX, startY, endX, endY))
    outline(color, thickness)(Matrix().mul(pixelsToScalar), dynamicMesh)
  }

  fun drawLine(start: Vector2, end: Vector2, color: Vector4, thickness: Float) {
    drawLine(start.x, start.y, end.x, end.y, color, thickness)
  }

  fun drawText(config: TextConfiguration) {
    val transform = prepareTextMatrix(pixelsToScalar, config.position)
    drawTextRaw(config, effects.coloredImage, vertexSchemas.image, transform)
  }

  fun drawText(position: Vector2, style: IndexedTextStyle, content: String) {
    val transform = prepareTextMatrix(pixelsToScalar, position)
    val textStyle = resolveTextStyle(fonts, style)
    drawTextRaw(TextConfiguration(content, position, textStyle), effects.coloredImage, vertexSchemas.image, transform)
  }

  fun drawText(position: Vector2i, style: IndexedTextStyle, content: String) =
      drawText(position.toVector2(), style, content)

  fun crop(value: Vector4i, action: () -> Unit) = cropStack(value, action)

  fun flipViewport(value: Vector4i): Vector4i =
      Vector4i(value.x, viewportDimensions.y.toInt() - value.y - value.w, value.z, value.w)
}
