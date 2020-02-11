plugins { kotlin("jvm") }

if (file("debug.txt").exists())
  requires(project, "debugging_debug")
else
  requires(project, "debugging_release")
