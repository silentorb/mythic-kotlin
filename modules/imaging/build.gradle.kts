plugins {
  kotlin("jvm") version "1.3.72"
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
  implementation("silentorb.mythic:randomly")
  implementation("silentorb.mythic:debugging")
  api("org.lwjgl:lwjgl:3.1.5")
  api("org.lwjgl:lwjgl:3.1.5:natives-windows")

  testImplementation("org.junit.jupiter:junit-jupiter:5.6.1")
  testImplementation("silentorb.imp:testing")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}
