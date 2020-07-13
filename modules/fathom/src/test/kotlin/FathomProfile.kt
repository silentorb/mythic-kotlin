import org.junit.jupiter.api.Test
import silentorb.imp.core.PathKey
import silentorb.imp.core.getGraphOutputNodes
import silentorb.imp.core.mergeNamespaces
import silentorb.imp.execution.executeToSingleValue
import silentorb.imp.execution.prepareExecutionUnit
import silentorb.imp.library.standard.standardLibrary
import silentorb.imp.parsing.parser.parseToDungeon
import silentorb.mythic.fathom.fathomLibrary
import silentorb.mythic.fathom.marching.marchingMesh
import silentorb.mythic.fathom.misc.ModelFunction
import silentorb.mythic.imaging.texturing.texturingLibrary

val impCode = """
import silentorb.mythic.fathom.*
import silentorb.mythic.generation.texturing.RgbColor
import silentorb.mythic.spatial.*

let dirtBump = noise
    scale = 74
    detail = 28
    variation = 112

let dirtColor = noise
    scale = 63
    detail = 78
    variation = 1
    . colorize (RgbColor 255 32 20) (RgbColor 30 50 0)

let squareWall = {
    let dimensions = Vector3 10.0 1.5 10.0
    let distance = cube dimensions

    let form = deform distance (dirtBump .* 0.2)

    let main = newModel form dirtColor (collisionBox dimensions)
}

""".trimIndent()

class FathomProfile {
  @Test
  fun profileWall() {
    val context = listOf(
      standardLibrary(),
      texturingLibrary(),
      fathomLibrary()
    )
    val (dungeon, errors) = parseToDungeon("", context)(impCode)
    if (errors.any())
      throw Error(errors.first().message.toString())

    val outputNode = PathKey("", "squareWall")
    val unit = prepareExecutionUnit(context + dungeon.namespace, outputNode)
    while (true) {
      val value = executeToSingleValue(unit)
      val model = value as ModelFunction
      val (vertices, triangles) = marchingMesh(10, model.form, model.shading)
    }
  }
}
