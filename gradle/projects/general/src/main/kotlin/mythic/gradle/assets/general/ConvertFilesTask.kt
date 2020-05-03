package mythic.gradle.assets.general

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileType
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.nio.file.Path
import java.nio.file.Paths

abstract class ConvertFilesTask : DefaultTask() {
  @get:Incremental
  @get:PathSensitive(PathSensitivity.NAME_ONLY)
  @get:InputDirectory
  abstract val inputDir: DirectoryProperty

  @get:OutputDirectory
  abstract val outputDir: DirectoryProperty

  abstract fun getArgs(inputPath: Path): List<String>

  @get:Input
  abstract val inputExtension: String

  fun getFlatOutputPath(fileName: String, outputExtension: String): Path =
      Paths.get(outputDir.file(".").get().asFile.absolutePath,fileName.split(".").first() + "." + outputExtension)

  @TaskAction
  fun execute(inputs: InputChanges) {
    for (change in inputs.getFileChanges(inputDir)) {
      if (change.fileType == FileType.DIRECTORY || change.file.extension != inputExtension) {
        println("Ignoring change ${change.file.name}")
      }
      else if (change.changeType == ChangeType.REMOVED) {
        println("Ignoring removed ${change.file.name}")
      } else {
        println("out of date: ${change.file.toPath()}")
        val args = getArgs(change.file.toPath())
        runProcess(args)
      }
    }
  }
}

abstract class ConvertFilesTaskSimple : ConvertFilesTask() {
  @get:Input
  abstract val executablePath: Property<String>
}
