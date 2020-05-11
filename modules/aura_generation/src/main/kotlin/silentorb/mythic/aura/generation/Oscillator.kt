package silentorb.mythic.aura.generation

fun oscillate(signalGenerator: SignalGenerator, frequency: Float): AudioSampler = { sampleRate, sampleTime ->
  val modulatedTime = ((sampleTime / sampleRate) % frequency.toDouble()).toFloat()
  signalGenerator(modulatedTime)
}
