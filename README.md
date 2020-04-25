# Mythic Game Engine - Kotlin

## Introduction

Mythic is a 3D game engine.  It is:

* Written in Kotlin, targeting the JVM
* Loosely-coupled, divided into dozens of modules with lightweight abstractions
* Uses functional paradigms
* At least 95% of its data structures are immutable
* Uses a table-based ECS (Entity Component System)
* Aside for a few special cases, all of its data handling is homogeneous (No mixed lists)
* Relies heavily on the most excellent [LWJGL](https://www.lwjgl.org/)
* No GUI tooling (if someone ever wants to make Mythic tooling, go for it!)
* Currently only supports OpenGL
* Currently only supports desktop and has only been tested on Windows

This project isn't being published with the expectation that people can grab it and build a game on top of it as-is.  It's not there.  It may eventually reach such a point, but right now it may be a useful grab bag of parts for anyone making a game with Kotlin, or a reference for how one might write a functional/ECS game.

## State of the Project

Initially this version of Mythic was created in a monorepo alongside one of the innumerable iterations of a Marloth game.  Now it's in the middle of being extracted into it's own repo.  Most of the general modules were already separate from the game and in their own mythic folder.  Some of the general modules were slightly more tied the to game but could easily be disconnected and moved here.

Much of Mythic as it is doesn't make sense and is incomplete without the Marloth codebase as at least a template.  The plan is to eventually release the Marloth code as open source similar to Id Software's release model.
