package voipot.jnu.conoz.com.voipotguide.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import voipot.jnu.conoz.com.voipotguide.room.entity.MessageEntity

@Dao
interface MessageDAO {
    @Insert
    fun insert(vararg message: MessageEntity)

    @Query("SELECT * FROM message WHERE id > :minimumId AND id <= :maximumId ORDER BY id DESC")
    fun findAllByIdRange(minimumId: Long, maximumId: Long): List<MessageEntity>

    @Query("SELECT count(*) FROM message")
    fun getCount(): Int

    @Query("UPDATE message SET isRead = 1 WHERE isRead = 0")
    fun updateRead()
}
