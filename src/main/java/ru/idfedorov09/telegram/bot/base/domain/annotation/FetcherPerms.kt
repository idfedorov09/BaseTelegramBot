package ru.idfedorov09.telegram.bot.base.domain.annotation

/**
 * Требуется для определения ролей юзера, который может использовать фетчер
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class FetcherPerms(vararg val roles: String)