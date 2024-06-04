package ru.idfedorov09.telegram.bot.base.domain.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Command(
    val command: String,
)
