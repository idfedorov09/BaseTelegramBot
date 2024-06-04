package ru.idfedorov09.telegram.bot.base.domain.service

import com.google.gson.Gson
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.idfedorov09.telegram.bot.base.domain.entity.UserEntity
import ru.idfedorov09.telegram.bot.base.repository.UserRepository
import kotlin.jvm.optionals.getOrNull


@Service
class UserService {
    @Autowired
    lateinit var userRepository: UserRepository<UserEntity>

    @Autowired
    lateinit var gson: Gson

    open fun findById(id: Long) = userRepository.findById(id).getOrNull()?.toDTO()

    open fun save(userEntity: UserEntity): UserEntity? {
        val lastUserEntity = userEntity.id?.let { userRepository.findById(it) }?.get()
        val target = JSONObject(userEntity.data)
        val source = JSONObject(lastUserEntity?.data)
        lastUserEntity ?: return userRepository.save(userEntity)
        val mergedData = deepMerge(source, target).toString()
        return userEntity.let {
            it.data = mergedData
            userRepository.save(it)
        }
    }

    open fun findNotDeletedByTui(tui: String) =
        userRepository.findNotDeletedByTui(tui)?.toDTO()

    open fun updateKeyboardSwitchedForUserTui(
        tui: String,
        isSwitched: Boolean,
    ) = userRepository.updateKeyboardSwitchedForUserTui(tui, isSwitched)

    private fun deepMerge(source: JSONObject, target: JSONObject): JSONObject {
        for (key in JSONObject.getNames(source)) {
            val value = source[key]
            if (!target.has(key)) {
                target.put(key, value)
            } else {
                if (value is JSONObject)
                    deepMerge(value, target.getJSONObject(key))
                else
                    target.put(key, value)
            }
        }
        return target
    }
}