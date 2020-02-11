plugins {
  kotlin("jvm") version "1.3.61"
}

group = "silentorb.mythic"

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  api("silentorb.mythic:spatial")
}
