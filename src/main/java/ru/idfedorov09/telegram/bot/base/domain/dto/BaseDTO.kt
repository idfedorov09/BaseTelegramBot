package ru.idfedorov09.telegram.bot.base.domain.dto

abstract class BaseDTO<E> {
    abstract fun toEntity(): E
}