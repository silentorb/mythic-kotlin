plugins {
  kotlin("jvm")
}

dependencies {
  api("org.lwjgl:lwjgl:${Versions.lwjgl}")
  runtimeOnly("org.lwjgl:lwjgl:${Versions.lwjgl}:${Natives.lwjgl}")
  implementation("org.lwjgl:lwjgl-glfw:${Versions.lwjgl}")
  runtimeOnly("org.lwjgl:lwjgl-glfw:${Versions.lwjgl}:${Natives.lwjgl}")
  implementation("org.lwjgl:lwjgl-stb:${Versions.lwjgl}")
  runtimeOnly("org.lwjgl:lwjgl-stb:${Versions.lwjgl}:${Natives.lwjgl}")
  implementation("org.lwjgl:lwjgl-openal:${Versions.lwjgl}")
  runtimeOnly("org.lwjgl:lwjgl-openal:${Versions.lwjgl}:${Natives.lwjgl}")
  implementation("silentorb.mythic:debugging")
  implementation("silentorb.mythic:platforming")
  implementation("silentorb.mythic:haft")
  implementation("silentorb.mythic:resource-loading")
}
