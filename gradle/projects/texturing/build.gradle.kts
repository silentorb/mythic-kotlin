plugins {
  kotlin("jvm") version "1.3.71"
}

group = "silentorb.mythic.gradle.assets"
version = "1.0"

dependencies {
  implementation(gradleApi())
  implementation(kotlin("stdlib"))
  implementation("silentorb.imp:parsing")
  implementation("silentorb.imp:execution")
  implementation("silentorb.imp:libraries_standard")
  implementation("silentorb.imp:libraries_standard_implementation")
  implementation("silentorb.mythic:imaging")
}

repositories {
  jcenter()
}

requires(project, "general")
