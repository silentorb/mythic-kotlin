plugins {
  kotlin("jvm") version "1.4.0"
}

group = "silentorb.mythic"

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  api("silentorb.mythic:scenery")
  api("silentorb.mythic:spatial")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}
