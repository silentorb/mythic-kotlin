plugins {
  kotlin("jvm") version "1.4.10"
}

group = "silentorb.mythic.gradle.assets"
version = "1.0"

dependencies {
  implementation(gradleApi())
  implementation(kotlin("stdlib"))
}

repositories {
  jcenter()
}
