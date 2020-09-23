# Mythic Godot

An experimental library to build Godot as a DLL that can integrate with the Mythic game engine.

* The goal of this project is not to provide general Godot API bindings
* This project is intended to provide a C++ core that drives Godot, and a specialized JNI API for the Mythic JVM pipeline to call
* For example, instead of providing an entire Graphics API, the JVM code will pass marshalled scene data to the custom C++ code which will in turn use that data to update and render an active Godot scene

## Building

From the Godot source directory run something like:

```
 scons -j4 custom_modules=../../mythic/mythic-godot/modules platform=windows tools=no target=release_debug
```
