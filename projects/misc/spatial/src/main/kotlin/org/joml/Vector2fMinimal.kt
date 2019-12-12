package org.joml

import mythic.spatial.Vector2

interface Vector2fMinimal {
  val x: Float
  val y: Float

  operator fun minus(v: Vector2fMinimal): Vector2

  fun xy(): Vector2
}

//interface Vector2fMinimalImmutable {
//  val x: Float
//  val y: Float
//
//  operator fun minus(v: Vector2fMinimal): Vector2
//
//  fun xy(): Vector2
//}