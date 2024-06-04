package ru.idfedorov09.telegram.bot.base.domain

import ru.idfedorov09.telegram.bot.base.config.registry.UserRole

object Roles {
    val USER = UserRole("USER", "Базовая роль")
    val ROOT = UserRole("ROOT", "Полный доступ")
}