package ru.idfedorov09.telegram.bot.base.flow

import org.springframework.context.annotation.Configuration
import ru.idfedorov09.telegram.bot.base.fetchers.ChooseFlowFetcher
import ru.mephi.sno.libs.flow.belly.FlowBuilder
import ru.mephi.sno.libs.flow.config.BaseFlowConfiguration

/**
 * Основной класс, в котором строится последовательность вычислений (граф) для бота
 */
@Configuration(proxyBeanMethods = false)
open class SystemTgFlowConfiguration(
    private val chooseFlowFetcher: ChooseFlowFetcher,
): BaseFlowConfiguration(SystemTgFlowConfiguration::class) {

    override fun FlowBuilder.buildFlow() {
        fetch(chooseFlowFetcher)
    }
}
