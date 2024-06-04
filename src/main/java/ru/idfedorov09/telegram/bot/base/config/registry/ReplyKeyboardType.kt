package ru.idfedorov09.telegram.bot.base.config.registry

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

data class ReplyKeyboardType(
    val type: String,
    /** Как клавиатура рисуется **/
    var builder: IReplyButtonBuilder,
): RegistryModel(ReplyKeyboardType::class, type) {
    init { registerModel() }

    fun build(roles: Set<UserRole>): ReplyKeyboardMarkup {
        val keyboard = builder.build(roles)
        val keyboardRows = keyboard.map { line ->
            KeyboardRow().also { row ->
                line.map { row.add(it.command) }
            }
        }
        return ReplyKeyboardMarkup().also {
            it.keyboard = keyboardRows
            it.resizeKeyboard = true
        }
    }
}