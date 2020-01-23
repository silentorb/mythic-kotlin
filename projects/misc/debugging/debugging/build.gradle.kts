apply(from = "${rootProject.projectDir}/build_kotlin.gradle")

if (file("debug.txt").exists())
  requires(project, "debugging_debug")
else
  requires(project, "debugging_release")
