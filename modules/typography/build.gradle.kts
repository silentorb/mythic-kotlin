plugins {
    kotlin("jvm") version "1.4.10"
}

group = "silentorb.mythic"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("org.lwjgl:lwjgl:3.2.3")
    implementation("org.lwjgl:lwjgl:3.2.3:natives-windows")
    implementation("org.lwjgl:lwjgl-opengl:3.2.3")
    implementation("org.lwjgl:lwjgl-opengl:3.2.3:natives-windows")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")
//    implementation("silentorb.mythic:typography-native")
}

dependencies {
    api("silentorb.mythic:glowing")
    api("silentorb.mythic:spatial")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
