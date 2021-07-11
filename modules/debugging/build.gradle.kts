plugins {
  kotlin("jvm") version "1.4.10"
}

group = "silentorb.mythic"

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  val subProject = if (file("debug.txt").exists())
    "debugging_debug"
  else
    "debugging_release"

  api(project(":$subProject"))
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}

