plugins {
  kotlin("jvm")
}

dependencies {
  api("org.lwjgl:lwjgl:${Versions.lwjgl}")
  api("org.lwjgl:lwjgl:${Versions.lwjgl}:${Natives.lwjgl}")
  implementation("org.lwjgl:lwjgl-glfw:${Versions.lwjgl}")
  implementation("org.lwjgl:lwjgl-glfw:${Versions.lwjgl}:${Natives.lwjgl}")
  implementation("org.lwjgl:lwjgl-stb:${Versions.lwjgl}")
  implementation("org.lwjgl:lwjgl-stb:${Versions.lwjgl}:${Natives.lwjgl}")
  implementation("org.lwjgl:lwjgl-openal:${Versions.lwjgl}")
  implementation("org.lwjgl:lwjgl-openal:${Versions.lwjgl}:${Natives.lwjgl}")
}

requires(project, "platforming", "haft")
