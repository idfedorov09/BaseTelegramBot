package ru.idfedorov09.telegram.bot.base.fetchers

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import ru.idfedorov09.telegram.bot.base.config.FetcherConfigContainer
import ru.idfedorov09.telegram.bot.base.config.registry.RegistryHolder
import ru.idfedorov09.telegram.bot.base.config.registry.UserRole
import ru.idfedorov09.telegram.bot.base.domain.Roles
import ru.idfedorov09.telegram.bot.base.domain.annotation.Callback
import ru.idfedorov09.telegram.bot.base.domain.annotation.CallbackDefault
import ru.idfedorov09.telegram.bot.base.domain.annotation.Command
import ru.idfedorov09.telegram.bot.base.domain.annotation.FetcherPerms
import ru.idfedorov09.telegram.bot.base.domain.annotation.InputText
import ru.idfedorov09.telegram.bot.base.domain.annotation.InputTextDefault
import ru.idfedorov09.telegram.bot.base.domain.dto.UserDTO
import ru.idfedorov09.telegram.bot.base.domain.service.CallbackDataService
import ru.idfedorov09.telegram.bot.base.domain.service.MessageSenderService
import ru.idfedorov09.telegram.bot.base.util.MessageParams
import ru.mephi.sno.libs.flow.belly.FlowContext
import ru.mephi.sno.libs.flow.fetcher.GeneralFetcher
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

@Component
open class DefaultFetcher : GeneralFetcher() {
    private lateinit var flowContext: FlowContext

    @Autowired
    private lateinit var messageSenderService: MessageSenderService
    @Autowired
    private lateinit var callbackDataService: CallbackDataService

    companion object {
        private val log = LoggerFactory.getLogger(DefaultFetcher::class.java)
    }

    override fun fetchCall(
        flowContext: FlowContext,
        doFetchMethod: KFunction<*>,
        params: MutableList<Any?>,
    ): Any? {
        this.flowContext = flowContext
        val exp = this.flowContext.get<FetcherConfigContainer>() ?: FetcherConfigContainer()
        if (!exp.shouldContinueExecutionFlow) {
            return null
        }
        if (!isValidPerms(flowContext, doFetchMethod)) return null

        return runCatching {
            super.fetchCall(flowContext, doFetchMethod, params).also {
                // всегда ли нужно handle() ?
                handle()
            }
        }.onFailure { e ->
            log.error("ERROR: $e")
            log.debug(e.stackTraceToString())
            stopFlowNextExecution()
        }.getOrNull()
    }

    /**
     * Вызывает методы-обработчики
     */

    // TODO : wrapper pattern + create ticket
    fun handle() {
        val update = flowContext.get<Update>() ?: run {
            log.warn("Can't handle update: there's no update in the context.")
            return
        }
        when {
            update.hasMessage() && update.message.hasText() -> textCommandsHandler(update)
            update.hasCallbackQuery() -> callbackQueryHandler(update)
        }
    }

    private fun callbackQueryHandler(update: Update) {
        val callbackId = update.callbackQuery.data?.toLongOrNull()
        callbackId ?: return
        val callbackData = callbackDataService.findById(callbackId)?.callbackData ?: return

        val commandMethods = this::class
            .declaredMemberFunctions
            .filter { it.hasAnnotation<Callback>() }

        val okCommandMethods = commandMethods.filter {
            it.findAnnotation<Callback>()?.let { cb -> cb.mark == callbackData } ?: false
        }

        if (okCommandMethods.size > 1)
            throw IllegalStateException("Too many matching commands in the fetcher. Check for prefix collisions.")

        if (okCommandMethods.size == 1) {
            val method = okCommandMethods.first()
            methodCall(method)
        }
        else {
            val defaultMethods = this::class
                .declaredMemberFunctions
                .filter { it.hasAnnotation<CallbackDefault>() }
            if (defaultMethods.size > 1)
                throw IllegalStateException("Too many matching default text-handlers in the fetcher.")
            if (defaultMethods.size == 1)
                methodCall(defaultMethods.first())
        }
    }

    // TODO: проверка ролей для команды
    private fun textCommandsHandler(update: Update) {
        val command = update.message.text.trim()

        val commandMethods = this::class
            .declaredMemberFunctions
            .filter { it.hasAnnotation<Command>() }

        val okCommandMethods = commandMethods.filter {
            it.findAnnotation<Command>()?.let { cmd -> command.startsWith(cmd.command) } ?: false
        }

        if (okCommandMethods.size > 1)
            throw IllegalStateException("Too many matching commands in the fetcher. Check for prefix collisions.")

        if (okCommandMethods.size == 1) {
            val method = okCommandMethods.first()
            methodCall(method)
        }
        else {
            // like an 'else' branch
            commonTextHandler(update)
        }
    }

    private fun commonTextHandler(update: Update) {
        val luatMark = flowContext.get<UserDTO>()?.lastUserActionType?.mark ?: return

        val methods = this::class
            .declaredMemberFunctions
            .filter { it.hasAnnotation<InputText>() }

        val okMethods = methods.filter {
            it.findAnnotation<InputText>()?.let { luat -> luatMark == luat.lastUserActionTypeMark } ?: false
        }

        if (okMethods.size > 1)
            throw IllegalStateException("Too many matching text-handlers in the fetcher. Check for the collisions.")

        if (okMethods.size == 1) {
            val method = okMethods.first()
            methodCall(method)
        } else {
            val defaultMethods = this::class
                .declaredMemberFunctions
                .filter { it.hasAnnotation<InputTextDefault>() }
            if (defaultMethods.size > 1)
                throw IllegalStateException("Too many matching default text-handlers in the fetcher.")
            if (defaultMethods.size == 1)
                methodCall(defaultMethods.first())
        }
    }

    private fun methodCall(method: KFunction<*>) {
        if (!isValidPerms(flowContext, method)) return
        val params = getParamsFromFlow(method, flowContext)
        val result = method.call(this, *params.toTypedArray())
        flowContext.insertObject(result)
    }

    private fun isValidPerms(
        flowContext: FlowContext,
        method: KFunction<*>,
    ): Boolean {
        val fetcherPermsAnnotation = method.findAnnotation<FetcherPerms>() ?: return true
        val user = flowContext.get<UserDTO>() ?: return true
        if (Roles.ROOT in user.roles) return true
        val allowPerms = fetcherPermsAnnotation.roles.map { RegistryHolder.getRegistry<UserRole>().get(it) }
        return allowPerms.all { it in user.roles }
    }

    /**
     * Прерывает дальнейшее выполнение графа в рамках сессии (прогонки графа)
     */
    @Synchronized
    fun stopFlowNextExecution() {
        val exp = flowContext.get<FetcherConfigContainer>() ?: FetcherConfigContainer()
        exp.apply {
            shouldContinueExecutionFlow = false
            flowContext.insertObject(this)
        }
    }

    /**
     * Метод который удаляет сообщение из только что пришедшего обновления
     * Вызывает исключение RuntimeException если в контексте нет Update или обновление не содержит сообщение
     */
    fun deleteUpdateMessage() {
        val update =
            flowContext.get<Update>()
                ?: throw RuntimeException("Can't delete the message: there's no update in the context.")

        if (!update.hasMessage()) {
            throw RuntimeException("Can't delete the message: there's no message in the update.")
        }

        messageSenderService.deleteMessage(
            MessageParams(
                chatId = update.message.chatId.toString(),
                messageId = update.message.messageId,
            ),
        )
    }
}