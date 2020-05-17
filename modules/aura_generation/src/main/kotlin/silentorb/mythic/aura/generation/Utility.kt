package silentorb.mythic.aura.generation

import silentorb.mythic.aura.generation.imp.AbsoluteTime

fun resolveTime(time: AbsoluteTime, sampleRate: SampleRate): Int =
    (time * sampleRate).toInt()
