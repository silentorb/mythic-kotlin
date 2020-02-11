plugins {
  kotlin("jvm")
}

requires(project, "ent", "spatial", "scenery", "sculpting")

dependencies {
  api("com.badlogicgames.gdx:gdx-bullet:${Versions.gdx}")
  api("com.badlogicgames.gdx:gdx-bullet-platform:${Versions.gdx}:natives-desktop")
}
