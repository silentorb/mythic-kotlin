package silentorb.mythic.happenings

fun <T> handleCommands(handler: (Command, T) -> T): (Commands, T) -> T = { commands, initial ->
  commands.fold(initial) { a, b -> handler(b, a) }
}
