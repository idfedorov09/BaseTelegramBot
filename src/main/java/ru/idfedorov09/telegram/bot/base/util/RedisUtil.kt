package ru.idfedorov09.telegram.bot.base.util

import redis.clients.jedis.Jedis
import ru.idfedorov09.telegram.bot.base.util.RedisServerData

object RedisUtil {
    fun getConnection(redisServerData: RedisServerData) = redisServerData.run {
        Jedis(host, port).also { jedis ->
            runCatching {
                password?.let { jedis.auth(it) }
            }
        }
    }
}
