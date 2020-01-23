import java.nio.file.Path

apply(from = "./utility.gradle.kts")

val scanProjects = extra["scanProjects"] as ((String, String) -> Unit) -> (Path) -> Unit

val addProjects = scanProjects { currentPath, name ->
  include(name)
  project(":$name").projectDir = File(currentPath)
}

addProjects(file("projects").toPath())
