package ru.idfedorov09.telegram.bot.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import static ru.idfedorov09.telegram.bot.base.domain.GlobalConstants.QUEUE_PRE_PREFIX;

@Component
@PropertySource("classpath:application.properties")
public class BotContainer {

	@Value("${telegram.bot.token}")
	public String BOT_TOKEN;

	@Value("${telegram.bot.name:test_snomephi_bot}")
	public String BOT_NAME;

	@Value("${telegram.bot.reconnect-pause:1000}")
	public int RECONNECT_PAUSE;

	public String getMessageQueuePrefix() {
		return BOT_NAME.concat(QUEUE_PRE_PREFIX);
	}
}
