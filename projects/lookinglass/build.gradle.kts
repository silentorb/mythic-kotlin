plugins {
  kotlin("jvm")
}

dependencies {
  implementation("com.fasterxml.jackson.core:jackson-databind:2.9.9.3")
  implementation("silentorb.mythic:debugging")
  api("silentorb.mythic:spatial")
  api("silentorb.mythic:scenery")
  api("silentorb.mythic:spatial_serialization")
  api("silentorb.mythic:configuration")
}

requires(project,"platforming", "sculpting", "glowing", "typography", "drawing",
    "breeze", "resource-loading")
