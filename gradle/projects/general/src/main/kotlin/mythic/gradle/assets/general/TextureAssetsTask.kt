package mythic.gradle.assets.general

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import java.nio.file.Path

abstract class TextureAssetsTask : ConvertFilesTaskSimple() {
  @get:Input
  abstract val projectDir: Property<String>

  override val inputExtension: String = "ffxml"

  override fun getArgs(inputPath: Path): List<String> {
    val outputFile = inputPath.fileName.toString().split(".").dropLast(1).joinToString(".")
    return listOf(executablePath.get(), "${projectDir.get()}/scripts/textures.py", outputFile)
  }
}
