package silentorb.mythic.happenings

fun <T> handleCommands(handler: (Command, T) -> T): (Commands, T) -> T = { commands, initial ->
  commands.fold(initial) { a, b -> handler(b, a) }
}

fun <Input, Output> onCommand(commands: Commands, commandType: Any, defaultValue: Output, handler: (Input) -> Output): Output {
  val command = commands.firstOrNull { it.type == commandType }
  return if (command != null)
    handler(command.value as Input)
  else
    defaultValue
}

fun <T> onSetCommand(commands: Commands, commandType: Any, defaultValue: T): T {
  val command = commands.firstOrNull { it.type == commandType }
  return if (command != null)
    command.value as T
  else
    defaultValue
}
