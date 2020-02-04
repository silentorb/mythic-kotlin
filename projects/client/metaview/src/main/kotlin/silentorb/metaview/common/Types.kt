package silentorb.metaview.common

import silentorb.metahub.core.Graph
import silentorb.metahub.core.OutputValues
import silentorb.metahub.core.Port
import silentorb.mythic.ent.Id

typealias CommonTransform = (CommonState) -> CommonState

const val bitmapType = "Bitmap"
const val colorType = "Color"
const val grayscaleType = "Grayscale"
const val floatType = "Float"
const val weightsType = "FloatList"
const val intType = "Int"
const val noneType = "None"
const val normalsType = "Normals"
const val positionsType = "Positions"
const val depthsType = "Depths"
const val multiType = "Multi"

data class GraphInteraction(
    val nodeSelection: List<Id> = listOf(),
    val portSelection: List<Port> = listOf(),
    val mode: GraphMode = GraphMode.normal
)

data class GuiState(
    val graphDirectory: String,
    val activeGraph: String? = null,
    val graphInteraction: GraphInteraction = GraphInteraction(),
    val previewFinal: Boolean = false
)

enum class GraphMode {
  connecting,
  normal
}

data class CommonState(
    val gui: GuiState,
    val graph: Graph? = null,
    val graphNames: List<String> = listOf(),
    val history: List<Graph> = listOf(),
    val future: List<Graph> = listOf(),
    val outputValues: OutputValues = mapOf()
)
