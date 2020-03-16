import org.gradle.internal.jvm.Jvm

plugins {
  `cpp-library`
  `java-library`
}

library {
  linkage.set(listOf(Linkage.STATIC))
  binaries.configureEach {
    compileTask.get().compilerArgs.addAll(compileTask.get().toolChain.map {
      if (it is Gcc || it is Clang) listOf("--std=c++11")
      else emptyList()
    })
    compileTask.get().compilerArgs.addAll(compileTask.get().targetPlatform.map {
      val include = when {
        it.operatingSystem.isMacOsX || it.operatingSystem.isLinux -> "-I"
        else -> "/I"
      }
      val subDir = when {
        it.operatingSystem.isMacOsX -> "darwin"
        it.operatingSystem.isLinux -> "linux"
        else -> "win32"
      }
      listOf(
          "/I ${Jvm.current().javaHome.canonicalPath}\\include",
          "/I ${Jvm.current().javaHome.canonicalPath}\\include\\$subDir"
      )
    })
  }
}

tasks.jar {
  from(library.developmentBinary.flatMap { (it as ComponentWithLinkFile).linkFile })
}
