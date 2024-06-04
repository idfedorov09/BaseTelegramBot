package ru.idfedorov09.telegram.bot.base.domain.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.idfedorov09.telegram.bot.base.controller.UpdatesController
import ru.idfedorov09.telegram.bot.base.domain.GlobalConstants.CURRENT_FLOW
import ru.idfedorov09.telegram.bot.base.flow.SystemTgFlowConfiguration
import ru.mephi.sno.libs.flow.belly.FlowBuilder
import ru.mephi.sno.libs.flow.registry.FlowRegistry

@Service
class FlowBuilderService(
    private val redisService: RedisService,
) {
    companion object {
        private val log = LoggerFactory.getLogger(UpdatesController::class.java)
        private val flowRegistry = FlowRegistry.getInstance()
    }

    fun getByName(name: String): FlowBuilder = flowRegistry.getFlow(name)

    fun getFlowBuilders(): List<String> = flowRegistry.flowNames

    fun getCurrentFlowName() = redisService.getSafe(CURRENT_FLOW)

    fun setSelectedFlow(name: String): Boolean{
        if (!getFlowBuilders().contains(name)) {
            log.warn("Flow doesn't exist: $name")
            return false
        }
        redisService.setValue(CURRENT_FLOW, name)
        return true
    }

    fun isFlowSelected() = !(getCurrentFlowName() == null || getCurrentFlowName() == getSystemFlowName())

    fun getSystemFlowName(): String = flowRegistry.getFlowName(SystemTgFlowConfiguration::class.java)
}