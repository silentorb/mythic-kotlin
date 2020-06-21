plugins {
  kotlin("jvm")
}

dependencies {
  api("org.lwjgl:lwjgl:${Versions.lwjgl}")
  api("org.lwjgl:lwjgl:${Versions.lwjgl}:${Natives.lwjgl}")
}
