plugins {
  kotlin("jvm")
}

requires(project, "imaging", "configuration")

dependencies {
  implementation("org.openjfx:javafx-base:11.0.1")
  implementation("org.openjfx:javafx-base:11.0.1:win")
  implementation("org.openjfx:javafx-controls:11.0.1")
  implementation("org.openjfx:javafx-controls:11.0.1:win")
  implementation("org.openjfx:javafx-graphics:11.0.1")
  implementation("org.openjfx:javafx-graphics:11.0.1:win")

  implementation("silentorb.imp.execution:1.0")
  implementation("silentorb.imp.parsing:1.0")
  implementation("silentorb.imp.libraries.standard:1.0")
  implementation("silentorb.imp.libraries.standard.implementation:1.0")
}
