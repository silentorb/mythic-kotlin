plugins {
    kotlin("jvm") version "1.4.0"
}

group = "silentorb.mythic"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    api("org.lwjgl:lwjgl:3.2.3")
    api("org.lwjgl:lwjgl:3.2.3:natives-windows")
    api("org.lwjgl:lwjgl-opengl:3.2.3")
    api("org.lwjgl:lwjgl-opengl:3.2.3:natives-windows")
}

dependencies {
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
