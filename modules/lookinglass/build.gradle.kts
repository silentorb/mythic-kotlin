plugins {
  kotlin("jvm") version "1.4.0"
}

group = "silentorb.mythic"

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  implementation("com.fasterxml.jackson.core:jackson-databind:2.9.9.3")
  implementation("silentorb.mythic:debugging")
  api("silentorb.mythic:spatial")
  api("silentorb.mythic:scenery")
  api("silentorb.mythic:spatial_serialization")
  api("silentorb.mythic:configuration")
  api("silentorb.mythic:platforming")
  api("silentorb.mythic:glowing")
  api("silentorb.mythic:typography")
  api("silentorb.mythic:drawing")
  api("silentorb.mythic:breeze")
  api("silentorb.mythic:resource-loading")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}
