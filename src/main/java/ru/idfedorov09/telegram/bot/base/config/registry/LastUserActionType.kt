package ru.idfedorov09.telegram.bot.base.config.registry

data class LastUserActionType(
    val type: String,
    val description: String? = null,
): RegistryModel(LastUserActionType::class, type) {
    init { registerModel() }
}