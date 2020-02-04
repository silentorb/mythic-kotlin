package silentorb.metaview.common


data class InputDefinition(
    val type: String,
    val defaultValue: Any? = null
)

data class NodeDefinition(
    val inputs: Map<String, InputDefinition>,
    val outputType: String,
    val outputs: Map<String, String> = mapOf(),
    val variableInputs: String? = null
)

typealias NodeDefinitionMap = Map<String, NodeDefinition>
