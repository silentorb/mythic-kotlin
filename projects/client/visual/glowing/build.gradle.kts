plugins {
    kotlin("jvm")
}

apply(from = "${rootProject.projectDir}/build_kotlin.gradle")

dependencies {
    api("org.lwjgl:lwjgl:${Versions.lwjgl}")
    api("org.lwjgl:lwjgl:${Versions.lwjgl}:${Natives.lwjgl}")
    api("org.lwjgl:lwjgl-opengl:${Versions.lwjgl}")
    api("org.lwjgl:lwjgl-opengl:${Versions.lwjgl}:${Natives.lwjgl}")
}

requires(project, "spatial", "glowing", "typography")
