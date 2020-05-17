package silentorb.mythic.aura.generation

import silentorb.mythic.randomly.Dice

fun oscillateInfinite(generator: (Double) -> (Float)): FrequencySignalGenerator = { frequency ->
  { sampleRate ->
    { position ->
      generator(position * frequency.toDouble() / sampleRate.toDouble())
    }
  }
}

val dice = Dice()
val randomSampler: AudioSampler = { sampleRate ->
  { _ ->
//  random(Dice(position.toLong()))
    random(dice)
  }
}
