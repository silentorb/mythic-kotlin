import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  base
  kotlin("jvm") version Versions.kotlin apply false
}

allprojects {
  group = "silentorb.mythic"
  version = "1.0"

  tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
  }

  repositories {
    jcenter()
    mavenCentral()
  }
}

subprojects {
  apply(plugin = "org.jetbrains.kotlin.jvm")

  dependencies {
    val implementation by configurations
    implementation(kotlin("stdlib-jdk8"))
  }
}
