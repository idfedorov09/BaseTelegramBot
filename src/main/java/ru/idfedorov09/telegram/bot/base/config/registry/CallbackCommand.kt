package ru.idfedorov09.telegram.bot.base.config.registry

data class CallbackCommand(
    val command: String,
    val description: String? = null,
): RegistryModel(CallbackCommand::class, command) {
    init { registerModel() }
}
