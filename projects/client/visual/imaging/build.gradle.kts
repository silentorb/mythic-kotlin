plugins {
  kotlin("jvm")
}

dependencies {
  implementation("silentorb.imp.execution:1.0")
}

requires(project, "ent", "spatial", "glowing", "randomly")
