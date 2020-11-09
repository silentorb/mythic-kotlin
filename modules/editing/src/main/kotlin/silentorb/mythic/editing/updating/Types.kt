package silentorb.mythic.editing.updating

import silentorb.mythic.editing.Editor
import silentorb.mythic.ent.Graph

typealias GraphEditCommandsHandler = (Editor, List<Any>, Graph) -> Graph

typealias EditorGraphTransform = (Editor, Graph) -> Graph
