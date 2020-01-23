plugins {
    kotlin("jvm")
}

apply(from = "${rootProject.projectDir}/build_kotlin.gradle")

dependencies {
    api("org.joml:joml:${Versions.joml}")
}
