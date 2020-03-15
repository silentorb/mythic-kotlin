val subProject = if (file("debug.txt").exists())
  "debugging_debug"
else
  "debugging_release"

include(subProject)
