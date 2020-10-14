plugins {
    kotlin("jvm") version "1.4.0"
}

group = "silentorb.mythic"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.9.3")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9")
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = "1.4.0")
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-afterburner", version = "2.9.9")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
