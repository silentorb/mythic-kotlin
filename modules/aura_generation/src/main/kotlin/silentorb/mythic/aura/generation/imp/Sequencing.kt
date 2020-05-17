package silentorb.mythic.aura.generation.imp

import silentorb.mythic.aura.generation.AudioSampler

fun renderPeriod(sampler: AudioSampler, floatStart: AbsoluteTime, floatEnd: AbsoluteTime): AudioSampler = { sampleRate ->
  val start = floatStart * sampleRate
  val getSample = sampler(sampleRate)
  val end = floatEnd * sampleRate
  { position ->
    if (position >= start && position < end)
      getSample(position)
    else
      0f
  }
}

