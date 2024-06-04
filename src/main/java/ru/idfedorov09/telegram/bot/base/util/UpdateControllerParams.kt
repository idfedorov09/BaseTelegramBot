package ru.idfedorov09.telegram.bot.base.util

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

data class UpdateControllerParams(
    val telegramBot: TelegramLongPollingBot,
    val update: Update,
)