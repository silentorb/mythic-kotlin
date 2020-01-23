plugins {
  kotlin("jvm")
}

apply(from = "${rootProject.projectDir}/build_kotlin.gradle")

dependencies {
  implementation("org.lwjgl:lwjgl:${Versions.lwjgl}")
  implementation("org.lwjgl:lwjgl:${Versions.lwjgl}:${Natives.lwjgl}")
  implementation("org.lwjgl:lwjgl-glfw:${Versions.lwjgl}")
  implementation("org.lwjgl:lwjgl-glfw:${Versions.lwjgl}:${Natives.lwjgl}")
  implementation("org.lwjgl:lwjgl-stb:${Versions.lwjgl}")
  implementation("org.lwjgl:lwjgl-stb:${Versions.lwjgl}:${Natives.lwjgl}")
  implementation("org.lwjgl:lwjgl-openal:${Versions.lwjgl}")
  implementation("org.lwjgl:lwjgl-openal:${Versions.lwjgl}:${Natives.lwjgl}")
}

requires(project, "platforming", "haft")
