package silentorb.mythic.aura.generation

fun oscillate(signalGenerator: SignalGenerator, frequency: Float): AudioSampler = { sampleRate, sampleTime ->
  val position = (sampleTime * frequency.toDouble() / sampleRate).toFloat()
  val modulatedTime = position % 1f
  signalGenerator(position)
}
