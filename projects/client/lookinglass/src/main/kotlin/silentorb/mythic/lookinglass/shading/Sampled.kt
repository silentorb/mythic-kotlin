package silentorb.mythic.lookinglass.shading

import silentorb.mythic.scenery.SamplePoint
import silentorb.mythic.scenery.Shading
import silentorb.mythic.spatial.toList

fun toFloatList(shading: Shading): List<Float> =
    toList(shading.color) + listOf(shading.opacity)

fun toFloatList(sample: SamplePoint) =
    toList(sample.location) + toList(sample.normal) + listOf(sample.size) + toFloatList(sample.shading)
