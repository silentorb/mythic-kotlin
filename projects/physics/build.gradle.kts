plugins {
  kotlin("jvm")
}

dependencies {
  api("com.badlogicgames.gdx:gdx-bullet:1.9.9")
  api("com.badlogicgames.gdx:gdx-bullet-platform:1.9.9:natives-desktop")
  api("silentorb.mythic:ent")
  api("silentorb.mythic:spatial")
  api("silentorb.mythic:scenery")
  api("silentorb.mythic:ent-scenery")
}

requires(project, "sculpting", "happenings")
