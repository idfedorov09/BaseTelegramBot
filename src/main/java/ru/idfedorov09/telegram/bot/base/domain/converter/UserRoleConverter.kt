package ru.idfedorov09.telegram.bot.base.domain.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import ru.idfedorov09.telegram.bot.base.config.registry.RegistryHolder
import ru.idfedorov09.telegram.bot.base.config.registry.UserRole

@Converter(autoApply = true)
class UserRoleConverter : AttributeConverter<UserRole, String> {

    private val unknown = "UNKNOWN_ROLE"

    override fun convertToDatabaseColumn(entity: UserRole?) = entity?.mark ?: unknown

    override fun convertToEntityAttribute(mark: String?) =
        mark?.let { RegistryHolder.getRegistry<UserRole>().get(it) }
}