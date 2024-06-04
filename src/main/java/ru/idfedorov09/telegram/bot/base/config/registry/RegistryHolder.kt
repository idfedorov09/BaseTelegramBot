package ru.idfedorov09.telegram.bot.base.config.registry

import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

object RegistryHolder {
    private val registries = mutableMapOf<KClass<out RegistryModel>, Registry<out RegistryModel>>()
    private val log = LoggerFactory.getLogger(this::class.java)

    init {
        registries[CallbackCommand::class] = Registry<CallbackCommand>()
        registries[UserRole::class] = Registry<UserRole>()
        registries[TextCommand::class] = Registry<TextCommand>()
        registries[LastUserActionType::class] = Registry<LastUserActionType>()
        registries[ReplyKeyboardType::class] = Registry<ReplyKeyboardType>()

        log.info("RegistryHolder has been initialized.")
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : RegistryModel> register(model: T) {
        val registryClass = model.getRegistryClass()
        val registry = registries[registryClass] as? Registry<T>
            ?: throw IllegalArgumentException("No registry found for $registryClass")
        registry.register(model)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : RegistryModel> getRegistry(kClass: KClass<T>): Registry<T> {
        return registries[kClass] as? Registry<T>
            ?: throw IllegalArgumentException("No registry found for ${kClass.qualifiedName}")
    }

    inline fun <reified T : RegistryModel> getRegistry(): Registry<T> = getRegistry(T::class)
}
