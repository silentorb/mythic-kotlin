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
  testImplementation("org.junit.jupiter:junit-jupiter:5.6.1")
  api("silentorb.mythic:randomly")
  implementation("silentorb.imp:execution")
  implementation("silentorb.imp:libraries_standard")
  implementation("silentorb.imp:libraries_standard_implementation")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}
