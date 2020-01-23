plugins {
  kotlin("jvm")
}

apply(from = "${rootProject.projectDir}/build_kotlin.gradle")

dependencies {
  implementation("com.fasterxml.jackson.core:jackson-databind:2.9.9.3")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9")
  implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}")
  implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-afterburner", version = "2.9.9")
}

requires(project,"platforming", "spatial", "scenery", "sculpting", "glowing", "typography", "drawing",
    "breeze", "configuration")
