plugins {
  kotlin("jvm") version "1.4.10"
}

group = "silentorb.mythic.gradle.assets"
version = "1.0"

dependencies {
  implementation(gradleApi())
  implementation(kotlin("stdlib"))
  implementation("silentorb.imp:parsing")
  implementation("silentorb.imp:execution")
  implementation("silentorb.imp:libraries_standard")
  implementation("silentorb.mythic:imaging")
}

repositories {
  jcenter()
}

requires(project, "general")
