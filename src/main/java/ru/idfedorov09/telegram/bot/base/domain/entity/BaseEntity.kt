package ru.idfedorov09.telegram.bot.base.domain.entity

abstract class BaseEntity<D>{
    abstract fun toDTO(): D
}