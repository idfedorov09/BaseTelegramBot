package ru.idfedorov09.telegram.bot.base.config.registry

data class UserRole(
    override val mark: String,
    val description: String? = null,
): RegistryModel(UserRole::class, mark) {
    init { registerModel() }
}