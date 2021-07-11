plugins {
  kotlin("jvm") version "1.4.10"
}

group = "silentorb.mythic"

repositories {
  jcenter()
  mavenCentral()
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}

dependencies {
  api("org.lwjgl:lwjgl:3.2.3")
  api("org.lwjgl:lwjgl:3.2.3:natives-windows")
}
