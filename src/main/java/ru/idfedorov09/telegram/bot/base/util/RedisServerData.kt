package ru.idfedorov09.telegram.bot.base.util

data class RedisServerData(
    val port: Int,
    val host: String,

    /**
     * hint: если пароль не указан, пробуем подключиться без него
     */
    val password: String?,
)
