
apply(from = "${rootProject.projectDir}/build_kotlin.gradle")

requires(project, "platforming", "spatial", "ent", "debugging")
