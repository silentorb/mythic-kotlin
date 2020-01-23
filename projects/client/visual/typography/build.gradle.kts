plugins {
    kotlin("jvm")
}

apply(from = "${rootProject.projectDir}/build_kotlin.gradle")

dependencies {
    implementation("org.lwjgl:lwjgl:${Versions.lwjgl}")
    implementation("org.lwjgl:lwjgl:${Versions.lwjgl}:${Natives.lwjgl}")
    implementation("org.lwjgl:lwjgl-opengl:${Versions.lwjgl}")
    implementation("org.lwjgl:lwjgl-opengl:${Versions.lwjgl}:${Natives.lwjgl}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}")
}

requires(project,"spatial", "glowing")
