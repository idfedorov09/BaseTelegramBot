package ru.idfedorov09.telegram.bot.base.fetchers

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import ru.idfedorov09.telegram.bot.base.domain.service.MessageSenderService
import ru.idfedorov09.telegram.bot.base.util.MessageParams
import ru.idfedorov09.telegram.bot.base.util.UpdatesUtil
import ru.mephi.sno.libs.flow.belly.InjectData

@Component
class HelloWorldFetcher(
    private val messageSenderService: MessageSenderService,
    private val updatesUtil: UpdatesUtil,
): DefaultFetcher() {

    @InjectData
    fun doFetch(update: Update) {
        messageSenderService.sendMessage(
            MessageParams(
                chatId = updatesUtil.getChatId(update)!!,
                text = "Hello World!"
            )
        )
    }
}