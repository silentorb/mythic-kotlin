import org.gradle.internal.jvm.Jvm

plugins {
  `visual-studio`
  `cpp-library`
  `cpp-unit-test`
//  `java-library`
}

library {
  binaries.configureEach {
    compileTask.get().compilerArgs.addAll(compileTask.get().toolChain.map {
      if (it is Gcc || it is Clang) listOf("--std=c++11")
      else emptyList()
    })
    compileTask.get().compilerArgs.addAll(compileTask.get().targetPlatform.map {
      listOf(
          "/I${Jvm.current().javaHome.canonicalPath}\\include",
          "/I${Jvm.current().javaHome.canonicalPath}\\include\\win32"
      )
    })
  }
}

//tasks.jar {
//  from(library.developmentBinary.flatMap { (it as ComponentWithRuntimeFile).runtimeFile })
//}
