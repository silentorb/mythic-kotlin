plugins {
  kotlin("jvm") version "1.4.0"
}

group = "silentorb.mythic"

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  api("io.imgui.java:imgui-java-binding:1.80-1.5.0")
  implementation("io.imgui.java:imgui-java-lwjgl3:1.80-1.5.0")
  runtimeOnly("io.imgui.java:imgui-java-natives-windows:1.80-1.5.0")

  implementation("org.lwjgl:lwjgl-glfw:3.2.3")
  runtimeOnly("org.lwjgl:lwjgl-glfw:3.2.3:natives-windows")

  implementation("org.lwjgl:lwjgl-opengl:3.2.3")
  runtimeOnly("org.lwjgl:lwjgl-opengl:3.2.3:natives-windows")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9")
  implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = "1.4.0")

  api("silentorb.mythic:spatial")
  api("silentorb.mythic:ent-scenery")
  implementation("silentorb.mythic:resource-loading")
  api("silentorb.mythic:spatial_serialization")
  implementation("silentorb.mythic:haft")
  api("silentorb.mythic:cameraman")
  api("silentorb.mythic:debugging")
  api("silentorb.mythic:scenery")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}
