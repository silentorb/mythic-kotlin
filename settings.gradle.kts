import java.nio.file.Files

includeBuild("../imp")

Files.list(file("modules").toPath())
    .forEach { path ->
      includeBuild(path)
    }


fun scanProjects(action: (String, String) -> Unit): (java.nio.file.Path) -> Unit = { path ->
  val name = path.toFile().name
  val currentPath = path.toFile().path.replace("\\", "/")
  val buildFilePath = "$currentPath/build.gradle"
  if (java.io.File(buildFilePath).exists() || java.io.File("$buildFilePath.kts").exists()) {
    action(currentPath, name)
  } else {
    Files.list(path)
        .filter { Files.isDirectory(it) }
        .forEach { file ->
          scanProjects(action)(file)
        }
  }
}

val addProjects = scanProjects { currentPath, name ->
  include(name)
  project(":$name").projectDir = java.io.File(currentPath)
}

addProjects(file("projects").toPath())
