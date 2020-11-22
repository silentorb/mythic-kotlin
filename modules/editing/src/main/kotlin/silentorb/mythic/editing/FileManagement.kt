package silentorb.mythic.editing

enum class FileItemType {
  folder,
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

fun copyRecursive(original: FileItems, item: FileItem, newParent: String): FileItems {
  val nextItem = item.copy(
      parent = newParent,
      fullPath = newParent + "/" + item.name
  )
  return original
      .filterValues { it.parent == item.fullPath }
      .map { child ->
        copyRecursive(original, child.value, nextItem.fullPath)
      }
      .fold(mapOf<String, FileItem>()) { a, b -> a + b }
      .plus(nextItem.fullPath to nextItem)
}

fun selectRecursive(items: FileItems, fullPath: String): Set<String> {
  return setOf(fullPath) +
      items
          .filterValues { it.parent == fullPath }
          .flatMap { child ->
            selectRecursive(items, child.key)
          }
}

fun isDerivativePath(shorter: String, longer: String): Boolean =
    shorter.length <= longer.length && longer.substring(0, shorter.length) == shorter

fun isParent(parent: String, child: String): Boolean =
    parent.length + 2 < child.length &&
        child.substring(0, parent.length + 1) == "$parent/" &&
        !child.substring(parent.length + 1).contains('/')