plugins {
  kotlin("jvm")
}

apply(from = "${rootProject.projectDir}/build_kotlin.gradle")

dependencies {
  implementation("io.github.cdimascio:java-dotenv:5.1.3")
}
