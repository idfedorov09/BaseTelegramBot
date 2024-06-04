package ru.idfedorov09.telegram.bot.base.config.registry

interface IReplyButtonBuilder {
    fun build(roles: Set<UserRole>): MutableList<MutableList<TextCommand>>
}