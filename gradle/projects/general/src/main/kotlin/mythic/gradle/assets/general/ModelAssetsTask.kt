package mythic.gradle.assets.general

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import java.nio.file.Path

abstract class ModelAssetsTask : ConvertFilesTaskSimple() {
  @get:Input
  abstract val projectDir: Property<String>

  override val inputExtension: String = "blend"

  override fun getArgs(inputPath: Path): List<String> {
    val scriptPath = "${projectDir.get()}/blend/scripts/export-2.8.py"
    return listOf(executablePath.get(), inputPath.toString(), "--background", "--python", scriptPath)
  }
}
