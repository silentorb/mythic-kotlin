plugins {
  kotlin("jvm") version "1.4.0"
}

group = "silentorb.mythic"

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  api("silentorb.mythic:glowing")
  api("silentorb.mythic:spatial")
  api("silentorb.mythic:typography")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}
