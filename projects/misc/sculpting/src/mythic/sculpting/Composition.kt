package mythic.sculpting

typealias Port = EdgeReference

//interface PortId {
//  val name: String
//}

//typealias Ports = Map<PortId, Port>

//data class MeshNode(
//    val mesh: FlexibleMesh,
//    val ports: Ports
//)

fun joinMeshNodes(first: FlexibleMesh, firstPort: Port, second: FlexibleMesh, secondPort: Port): FlexibleMesh {
//  assert(firstPort.size == secondPort.size)
  val mesh = FlexibleMesh()
  val firstLoop = getEdgeLoop(firstPort)
  val secondLoop = getEdgeLoopReversed(secondPort)
  setAnchor(getEdgesCenter(firstLoop), first.distinctVertices)
  setAnchor(getEdgesCenter(secondLoop), second.distinctVertices)
  mesh.sharedImport(first)
  mesh.sharedImport(second)

  stitchEdgeLoops(firstLoop, secondLoop)

  return mesh
}

fun join3DPaths(first: Vertices3m, second: Vertices3m) {

}