package ru.idfedorov09.telegram.bot.base.domain.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.idfedorov09.telegram.bot.base.domain.dto.CallbackDataDTO
import ru.idfedorov09.telegram.bot.base.domain.entity.CallbackDataEntity
import ru.idfedorov09.telegram.bot.base.repository.CallbackDataRepository
import kotlin.jvm.optionals.getOrNull

@Service
open class CallbackDataService {
    @Autowired
    lateinit var callbackDataEntityRepository: CallbackDataRepository<CallbackDataEntity>

    open fun findById(id: Long) =
        callbackDataEntityRepository.findById(id)
            .getOrNull()
            ?.toDTO()

    open fun save(callbackDataEntity: CallbackDataEntity): CallbackDataEntity? =
        callbackDataEntityRepository.save(callbackDataEntity)

    open fun save(callbackData: CallbackDataDTO): CallbackDataDTO? =
        save(callbackData.toEntity())
            ?.toDTO()
}