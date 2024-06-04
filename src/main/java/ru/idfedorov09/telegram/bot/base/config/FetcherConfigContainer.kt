package ru.idfedorov09.telegram.bot.base.config

import ru.mephi.sno.libs.flow.belly.Mutable

@Mutable
data class FetcherConfigContainer(
    /** флаг принудительной остановки флоу. Если false то флоу принудительно останавливается **/
    var shouldContinueExecutionFlow: Boolean = true,
)