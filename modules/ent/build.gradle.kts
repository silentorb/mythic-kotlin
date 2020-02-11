plugins {
  kotlin("jvm") version "1.3.61"
}

//dependencies {
//  implementation(kotlin("stdlib"))
//}
group = "silentorb.mythic"

repositories {
  jcenter()
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-reflect")
}
//project.dependencies.add("implementation", "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
