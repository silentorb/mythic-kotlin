plugins {
  kotlin("jvm") version "1.4.10"
}

group = "silentorb.mythic"

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  implementation("silentorb.imp:execution")
  implementation("silentorb.imp:libraries_standard")
  api("silentorb.mythic:ent")
  api("silentorb.mythic:spatial")
  api("silentorb.mythic:randomly")
  implementation("silentorb.mythic:debugging")
  api("org.lwjgl:lwjgl:3.2.3")
  api("org.lwjgl:lwjgl:3.2.3:natives-windows")
  api("silentorb.mythic:imaging")
  api("silentorb.mythic:scenery")
  api("silentorb.mythic:mythic-shape-meshes")

  testImplementation("org.junit.jupiter:junit-jupiter:5.6.1")
  testImplementation("silentorb.imp:testing")
  testImplementation("silentorb.imp:campaign")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}
