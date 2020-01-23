plugins {
  kotlin("jvm")
}

apply(from = "${rootProject.projectDir}/build_kotlin.gradle")

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}")
}
