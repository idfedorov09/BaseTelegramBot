package ru.idfedorov09.telegram.bot.base.domain

import ru.idfedorov09.telegram.bot.base.config.registry.TextCommand

object Commands {
    val CHANGE_FLOW_COMMAND = TextCommand(
        command = "/flow",
        description = "Меняет флоу",
        showInHelp = true,
    )
}