
plugins { kotlin("jvm") }

dependencies {
  implementation("silentorb.mythic:debugging")
  api("silentorb.mythic:happenings")
}

requires(project, "platforming", "spatial", "ent")
