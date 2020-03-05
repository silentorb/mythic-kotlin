plugins {
  kotlin("jvm") version "1.3.61"
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
