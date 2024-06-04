package ru.idfedorov09.telegram.bot.base.config

import jakarta.annotation.PostConstruct
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import ru.idfedorov09.telegram.bot.base.config.registry.RegistryHolder
import ru.idfedorov09.telegram.bot.base.domain.Commands
import ru.idfedorov09.telegram.bot.base.domain.GlobalConstants
import ru.idfedorov09.telegram.bot.base.domain.Roles
import ru.mephi.sno.libs.flow.config.BaseFlowConfiguration

@Component
open class FlowConfig(val applicationContext: ApplicationContext) {

    @PostConstruct
    fun configure() {
        buildFlows()
        registryInit()
    }

    private fun buildFlows() {
        val flowBeans = applicationContext.getBeansOfType(BaseFlowConfiguration::class.java)
        flowBeans.values.forEach {
            it.flowBuilder()
        }
    }

    private fun registryInit() {
        RegistryHolder
        Commands
        GlobalConstants
        Roles
    }
}
