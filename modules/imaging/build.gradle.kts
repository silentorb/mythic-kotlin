plugins {
  kotlin("jvm") version "1.3.61"
}

group = "silentorb.mythic"

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  testApi("junit:junit:4.13")
  implementation(kotlin("stdlib-jdk8"))
  implementation("silentorb.imp:execution")
  implementation("silentorb.imp:libraries_standard")
  implementation("silentorb.imp:libraries_standard_implementation")
  testImplementation("silentorb.imp:testing")
  api("silentorb.mythic:ent")
  api("silentorb.mythic:spatial")
  api("silentorb.mythic:randomly")
  api("silentorb.mythic:debugging")
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
