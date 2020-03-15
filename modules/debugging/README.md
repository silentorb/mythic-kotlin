# Debugging Module

Create a file named `debug.txt` in this directory to enable debug mode.
Otherwise all of the functions for this module are stubs.

To perform switching between debug and release versions of debug functions, two implementation modules are being used.
This is working better than dynamically switching which source files are used, which seemed to confuse both Gradle and IntelliJ.
Now the switch is on dependencies, which seems to be the lightest point of the chain to have a switch.

Right now the reliance on a file as a switch condition is a hack since I can't find a simple means of
setting Gradle arguments from IntelliJ's integration of Gradle into its build system.
You can specify custom Gradle configurations but those don't seem to integrate with IntelliJ's Gradle import operations.  
