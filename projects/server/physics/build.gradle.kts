plugins {
  kotlin("jvm")
}

requires(project, "ent", "spatial", "scenery", "sculpting", "happenings")

dependencies {
  api("com.badlogicgames.gdx:gdx-bullet:${Versions.gdx}")
  api("com.badlogicgames.gdx:gdx-bullet-platform:${Versions.gdx}:natives-desktop")
}
