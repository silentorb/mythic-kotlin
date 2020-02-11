plugins {
    kotlin("jvm")
}

dependencies {
    implementation("org.lwjgl:lwjgl:${Versions.lwjgl}")
    implementation("org.lwjgl:lwjgl:${Versions.lwjgl}:${Natives.lwjgl}")
    implementation("org.lwjgl:lwjgl-opengl:${Versions.lwjgl}")
    implementation("org.lwjgl:lwjgl-opengl:${Versions.lwjgl}:${Natives.lwjgl}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}")
}

requires(project,"spatial", "glowing")
