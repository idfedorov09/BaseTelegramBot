package ru.idfedorov09.telegram.bot.base.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import ru.idfedorov09.telegram.bot.base.domain.entity.UserEntity

interface UserRepository<T: UserEntity> : JpaRepository<T, Long> {

    @Query(
        """
            SELECT *
            FROM users_table
            WHERE 1 = 1
                and tui = :userTui
                and is_deleted = False
            LIMIT 1
        """,
        nativeQuery = true,
    )
    fun findNotDeletedByTui(userTui: String): T?

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Modifying
    @Query(
        """
            UPDATE UserEntity u 
            SET u.isKeyboardSwitched = :isSwitched 
            WHERE u.tui = :tui
        """,
    )
    fun updateKeyboardSwitchedForUserTui(
        tui: String,
        isSwitched: Boolean,
    )
}
