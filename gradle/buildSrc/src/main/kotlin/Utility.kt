import org.gradle.api.Project

fun requires(project: Project, vararg names: String) {
  names.forEach { project.dependencies.add("api", project.project(":" + it)) }
}
