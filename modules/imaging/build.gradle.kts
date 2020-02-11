plugins {
  kotlin("jvm") version "1.3.61"
}

group = "silentorb.mythic"

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  implementation("silentorb.imp:execution")
  api("silentorb.mythic:ent")
  api("silentorb.mythic:spatial")
  api("silentorb.mythic:randomly")
}
