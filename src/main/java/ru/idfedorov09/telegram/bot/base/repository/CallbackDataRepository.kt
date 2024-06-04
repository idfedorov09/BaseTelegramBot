package ru.idfedorov09.telegram.bot.base.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.idfedorov09.telegram.bot.base.domain.entity.CallbackDataEntity

interface CallbackDataRepository<T: CallbackDataEntity> : JpaRepository<T, Long>