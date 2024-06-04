package ru.idfedorov09.telegram.bot.base.domain.service

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Message
import ru.idfedorov09.telegram.bot.base.config.registry.RegistryHolder
import ru.idfedorov09.telegram.bot.base.config.registry.ReplyKeyboardType
import ru.idfedorov09.telegram.bot.base.executor.Executor
import ru.idfedorov09.telegram.bot.base.util.MessageParams
import ru.idfedorov09.telegram.bot.base.util.MessageSenderUtil

@Service
open class MessageSenderService(
    private val bot: Executor,
    private val userService: UserService,
) {

    /**
     * Отправляет пользователю сообщение
     * Выставляет клавиатуру, если это требуется
     */
    fun sendMessage(messageParams: MessageParams): Message {
        return messageParams.run {
            if (userService.findNotDeletedByTui(messageParams.chatId) == null) {
                MessageSenderUtil.sendMessage(bot, messageParams)
            } else {
                trySendWithSwitchKeyboard(messageParams)
            }
        }
    }

    /**
     * Пробует отправить сообщение + проставить клавиатуру
     */
    private fun trySendWithSwitchKeyboard(messageParams: MessageParams): Message {
        val chatId = messageParams.chatId.toLongOrNull()
        if (chatId == null || chatId < 0 || messageParams.replyMarkup != null) {
            return MessageSenderUtil.sendMessage(bot, messageParams)
        }
        val user =
            userService.findNotDeletedByTui(messageParams.chatId)
                ?: throw Exception("User not found by tui=$chatId")
        if (user.isKeyboardSwitched) {
            return MessageSenderUtil.sendMessage(bot, messageParams)
        }
        val keyboard = RegistryHolder
            .getRegistry<ReplyKeyboardType>()
            .get(user.currentKeyboardType?.mark)
            ?.build(user.roles)

        val messageParamsWithKeyboard = messageParams.copy(replyMarkup = keyboard)

        return MessageSenderUtil.sendMessage(bot, messageParamsWithKeyboard).also {
            userService.updateKeyboardSwitchedForUserTui(messageParams.chatId, true)
        }
    }

    fun editMessage(messageParams: MessageParams): Message {
        return MessageSenderUtil.editMessageText(bot, messageParams)
    }

    fun editMessageReplyMarkup(messageParams: MessageParams) {
        MessageSenderUtil.editMessageReplyMarkup(bot, messageParams)
    }

    fun deleteMessage(messageParams: MessageParams) {
        MessageSenderUtil.deleteMessage(bot, messageParams)
    }
}
