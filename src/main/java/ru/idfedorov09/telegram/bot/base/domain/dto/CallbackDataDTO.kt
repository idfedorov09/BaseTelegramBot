package ru.idfedorov09.telegram.bot.base.domain.dto

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import ru.idfedorov09.telegram.bot.base.domain.entity.CallbackDataEntity

data class CallbackDataDTO(
    val id: Long? = null,
    val chatId: String? = null,
    val messageId: String? = null,
    val callbackData: String? = null,
    val metaText: String? = null,
    val metaUrl: String? = null,
): BaseDTO<CallbackDataEntity>() {
    /**
     * Создает кнопку, которуж можно добавить в клавиатуру и отправить пользователю
     */
    fun createKeyboard() = InlineKeyboardButton().also {
        it.text = metaText!!
        it.callbackData = id!!.toString()
        it.url = metaUrl
    }

    override fun toEntity() = CallbackDataEntity(
        id = id,
        chatId = chatId,
        messageId = messageId,
        callbackData = callbackData,
        metaText = metaText,
        metaUrl = metaUrl,
    )
}
