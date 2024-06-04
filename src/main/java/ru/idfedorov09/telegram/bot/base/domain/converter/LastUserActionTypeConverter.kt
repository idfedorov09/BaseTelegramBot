package ru.idfedorov09.telegram.bot.base.domain.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import ru.idfedorov09.telegram.bot.base.config.registry.LastUserActionType
import ru.idfedorov09.telegram.bot.base.config.registry.RegistryHolder

@Converter(autoApply = true)
class LastUserActionTypeConverter : AttributeConverter<LastUserActionType, String> {
    override fun convertToDatabaseColumn(attribute: LastUserActionType?): String {
        return attribute?.mark ?: "DEFAULT"
    }

    override fun convertToEntityAttribute(mark: String?) =
        mark?.let { RegistryHolder.getRegistry<LastUserActionType>().get(it) }
}
