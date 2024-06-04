package ru.idfedorov09.telegram.bot.base.domain.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import ru.idfedorov09.telegram.bot.base.config.registry.RegistryHolder
import ru.idfedorov09.telegram.bot.base.config.registry.ReplyKeyboardType

@Converter(autoApply = true)
class UserKeyboardTypeConverter : AttributeConverter<ReplyKeyboardType, String> {
    override fun convertToDatabaseColumn(attribute: ReplyKeyboardType?): String {
        return attribute?.mark ?: "DEFAULT"
    }

    override fun convertToEntityAttribute(mark: String?) =
        mark?.let { RegistryHolder.getRegistry<ReplyKeyboardType>().get(it) }
}