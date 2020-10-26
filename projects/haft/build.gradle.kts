
plugins { kotlin("jvm") }

dependencies {
  implementation("silentorb.mythic:debugging")
}

requires(project, "platforming", "spatial", "ent")
