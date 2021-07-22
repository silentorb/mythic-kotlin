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
  api("org.slf4j:slf4j-api:1.7.31")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}

configurations {
  all {
    // It seems like a design flaw in slf4j that I should need this line,
    // but it's a commonly documented fix for slf4j.
    // It seems like this module should be something opted in instead of opted out.
    exclude(module = "slf4j-log4j12")
  }
}
