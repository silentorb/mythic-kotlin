package silentorb.mythic.lookinglass

import silentorb.mythic.breeze.AnimationInfo
import silentorb.mythic.breeze.AnimationInfoMap
import silentorb.mythic.scenery.ArmatureName

fun mapAnimationInfo(armatures: Map<ArmatureName, Armature>): AnimationInfoMap =
    armatures
        .flatMap { (_, armature) ->
          armature.animations.map { (key, value) ->
            Pair(key, AnimationInfo(
                duration = value.duration,
                markers = value.markers
            ))
          }
        }
        .associate { it }
