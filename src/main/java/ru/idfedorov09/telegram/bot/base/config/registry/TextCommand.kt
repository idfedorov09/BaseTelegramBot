package ru.idfedorov09.telegram.bot.base.config.registry

import ru.idfedorov09.telegram.bot.base.domain.Roles
import ru.idfedorov09.telegram.bot.base.domain.dto.UserDTO
import ru.idfedorov09.telegram.bot.base.domain.entity.UserEntity

data class TextCommand(
    /** текст команды **/
    val command: String,
    /** роли которым доступна эта команда **/
    val allowedRoles: List<UserRole> = listOf(Roles.USER),
    /** описание **/
    val description: String? = "",
    /** использование в меню помощи **/
    val showInHelp: Boolean = true,
) : RegistryModel(TextCommand::class, command) {
    init { registerModel() }

    companion object {
        fun isTextCommand(text: String?) =
            text?.let {
                RegistryHolder.getRegistry<TextCommand>().getAll().any {
                    text.trim().startsWith(it.command)
                }
            } ?: false
    }

    fun hasAccess(roles: Set<UserRole>) = roles.any { currentUserRole ->
        allowedRoles.contains(currentUserRole)
    }

    fun hasAccess(user: UserDTO) = hasAccess(user.roles)
    fun hasAccess(userEntity: UserEntity) = hasAccess(userEntity.roles)
}