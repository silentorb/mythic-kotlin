
plugins { kotlin("jvm") }

dependencies {
  api("silentorb.mythic:debugging")
}

requires(project, "platforming", "spatial", "ent")
