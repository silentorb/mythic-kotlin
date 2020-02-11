import org.gradle.api.Project
import java.io.File

fun requires(project: Project, vararg names: String) {
  names.forEach {
    val target = if (File("${project.rootProject.projectDir}/modules/$it").exists())
      "silentorb.mythic:$it"
    else
      project.project(":$it")
    project.dependencies.add("api", target)
  }
}
