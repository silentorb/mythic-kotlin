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
  api("silentorb.mythic:randomly")
  implementation("silentorb.imp:execution")
  implementation("silentorb.imp:libraries_standard")
  implementation("org.lwjgl:lwjgl:3.1.5")
  implementation("org.lwjgl:lwjgl:3.1.5:natives-windows")

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
