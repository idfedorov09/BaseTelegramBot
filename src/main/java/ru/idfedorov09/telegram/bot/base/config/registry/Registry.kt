package ru.idfedorov09.telegram.bot.base.config.registry

import org.slf4j.LoggerFactory

class Registry<T : RegistryModel> {
    private val log = LoggerFactory.getLogger(this::class.java)
    private val modelMap = HashMap<String, T>()

    fun register(model: T) {
        if (modelMap.containsKey(model.mark))
            throw IllegalArgumentException("Model ${model.mark} already exists.")
        modelMap[model.mark] = model
        log.debug("Model registered successfully: {}", model)
    }

    fun get(mark: String?): T? = mark?.let { modelMap[mark] }
    fun getAll(): List<T> = modelMap.values.toList()
}
