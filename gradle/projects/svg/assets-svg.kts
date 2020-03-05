abstract class ConvertFilesTask : DefaultTask() {
  @get:Incremental
  @get:PathSensitive(PathSensitivity.NAME_ONLY)
  @get:InputDirectory
  abstract val inputDir: DirectoryProperty

  @get:OutputDirectory
  abstract val outputDir: DirectoryProperty

  lateinit var executablePath: String

  abstract fun getArgs(fileName: String): List<String>

  @TaskAction
  fun execute(inputs: InputChanges) {
    inputs.getFileChanges(inputDir).forEach { change ->
      if (change.fileType == FileType.DIRECTORY) return@forEach
      if (change.changeType == ChangeType.REMOVED) {

      } else {
        val fileName = change.file.name
        println("out of date: ${fileName}")
        println(args.joinToString(" "))
        val args = getArgs(fileName)
        val process = ProcessBuilder(args)
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()
        if (!process.waitFor(10, TimeUnit.SECONDS)) {
          process.destroy()
          throw RuntimeException("execution timed out: $this")
        }
        if (process.exitValue() != 0) {
          throw RuntimeException("execution failed with code ${process.exitValue()}: $this")
        }
      }
    }
  }
}

abstract class SvgTask : ConvertFilesTask() {
  override fun getArgs(fileName: String): List<String> {
    val ext = "png"
    val inputPath = outputDir.file(".").get().asFile.absolutePath + "/" + fileName
    val outputPath = outputDir.file(".").get().asFile.absolutePath + "/" + fileName.split(".").first() + "." + ext
    return listOf(executablePath, "--export-png=$", outputPath, "-w", "1024", "-h", "1024", inputPath)
  }
}
