plugins {
  kotlin("jvm") version "1.3.61"
}

group = "silentorb.mythic"

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  implementation("silentorb.imp:execution")
  api("silentorb.mythic:ent")
  api("silentorb.mythic:spatial")
  api("silentorb.mythic:randomly")
  api("org.lwjgl:lwjgl:3.1.5")
  api("org.lwjgl:lwjgl:3.1.5:natives-windows")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}
