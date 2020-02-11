plugins {
  kotlin("jvm") version Versions.kotlin apply false
}

allprojects {
  group = "silentorb.mythic"
  version = "1.0"

  repositories {
    jcenter()
    mavenCentral()
  }

}

subprojects {
  apply(plugin = "org.jetbrains.kotlin.jvm")

  dependencies {
    val implementation by configurations
    implementation(kotlin("stdlib"))
  }
}
