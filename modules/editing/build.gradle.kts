plugins {
  kotlin("jvm") version "1.4.0"
}

group = "silentorb.mythic"

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  implementation("io.imgui.java:imgui-java-binding:1.78-1.3.0")
  implementation("io.imgui.java:imgui-java-lwjgl3:1.78-1.3.0")
  runtimeOnly("io.imgui.java:imgui-java-natives-windows:1.78-1.3.0")

  implementation("org.lwjgl:lwjgl-glfw:3.2.3")
  runtimeOnly("org.lwjgl:lwjgl-glfw:3.2.3:natives-windows")

  implementation("org.lwjgl:lwjgl-opengl:3.2.3")
  runtimeOnly("org.lwjgl:lwjgl-opengl:3.2.3:natives-windows")

  api("silentorb.mythic:spatial")
  implementation("silentorb.mythic:resource-loading")
  api("silentorb.mythic:spatial_serialization")
  implementation("silentorb.mythic:haft")
  api("silentorb.mythic:cameraman")
  api("silentorb.mythic:debugging")
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}
