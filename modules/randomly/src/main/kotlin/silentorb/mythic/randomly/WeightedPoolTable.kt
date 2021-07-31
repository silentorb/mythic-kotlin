package silentorb.mythic.randomly

data class WeightedPoolTable(
    val alias: List<Int>,
    val probability: List<Int>,
    val total: Int,
)

fun newWeightedPoolTable(probabilities: List<Int>): WeightedPoolTable {
  require(probabilities.any()) { "Alias table probabilities list cannot be empty." }
  val alias = IntArray(probabilities.size)
  val probability = IntArray(probabilities.size)
  val tempProbabilities = probabilities.toMutableList()
  val total = tempProbabilities.sum()
  val average = total / tempProbabilities.size

  val stacks = tempProbabilities
      .indices
      .partition { tempProbabilities[it] >= average }

  val large = stacks.first.toMutableList()
  val small = stacks.second.toMutableList()

  while (small.any() && large.any()) {
    val less = small.removeLast()
    val more = large.removeLast()
    probability[less] = tempProbabilities[less] * tempProbabilities.size
    alias[less] = more
    tempProbabilities[more] = tempProbabilities[more] + tempProbabilities[less] - average

    if (tempProbabilities[more] >= average)
      large.add(more)
    else
      small.add(more)
  }

  small.forEach { probability[it] = total }
  large.forEach { probability[it] = total }

  return WeightedPoolTable(
      alias = alias.asList(),
      probability = probability.asList(),
      total = total,
  )
}

fun getAliasedIndex(table: WeightedPoolTable, dice: Dice): Int {
  val column = dice.getInt(table.probability.size - 1)
  val coinToss = dice.getInt(table.total) < table.probability[column]
  return if (coinToss)
    column
  else
    table.alias[column]
}
