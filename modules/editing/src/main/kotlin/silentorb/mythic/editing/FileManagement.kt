package silentorb.mythic.editing

enum class FileItemType {
  directory,
  file,
}

data class FileItem(
    val type: FileItemType,
    val fullPath: String,
    val name: String,
    val parent: String?
)

typealias FileItems = Map<String, FileItem>

fun newFileItem(fullPath: String, type: FileItemType): FileItem {
  val parts = fullPath.split('/')
  assert(parts.any())
  val parentTokens = parts.dropLast(1)
  val parent = if (parentTokens.none())
    null
  else
    parentTokens.joinToString("/")

  return FileItem(
      fullPath = fullPath,
      parent = parent,
      name = parts.last(),
      type = type,
  )
}
