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
  api("silentorb.mythic:ent")
  api("silentorb.mythic:spatial")
  api("silentorb.mythic:scenery")
  api("silentorb.mythic:lookinglass")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}
