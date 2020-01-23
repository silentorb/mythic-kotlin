import java.nio.file.Files
import java.nio.file.Path

typealias ScanProjects = ((String, String) -> Unit) -> (Path) -> Unit

fun scanProjects(action: (String, String) -> Unit): (Path) -> Unit = { path ->
  val name = path.toFile().name
  val currentPath = path.toFile().path.replace("\\", "/")
  val buildFilePath = "$currentPath/build.gradle"
  if (File(buildFilePath).exists() || File("$buildFilePath.kts").exists()) {
    action(currentPath, name)
  } else {
    Files.list(path)
        .filter { Files.isDirectory(it) }
        .forEach { file ->
          scanProjects(action)(file)
        }
  }
}

extra["scanProjects"] = ::scanProjects
