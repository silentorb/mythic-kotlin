package mythic.gradle.assets.general

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import java.nio.file.Path

abstract class AudioAssetsTask : ConvertFilesTaskSimple() {
  @get:Input
  abstract val projectDir: Property<String>

  override val inputExtension: String = "scd"

  override fun getArgs(inputPath: Path): List<String> {
    return listOf(executablePath.get(), "${projectDir.get()}/scripts/audio.py", inputPath.fileName.toString().split(".").first())
  }
}
