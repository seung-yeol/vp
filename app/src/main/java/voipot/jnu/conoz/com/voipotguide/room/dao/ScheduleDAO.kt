package voipot.jnu.conoz.com.voipotguide.room.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import voipot.jnu.conoz.com.voipotguide.room.entity.*

@Dao
interface ScheduleDAO{
    @Insert
    fun add(scheduleEntity: ScheduleEntity): Long

    @Insert
    fun addAll(scheduleList: List<ScheduleEntity>)

    @Query("DELETE FROM $TABLE_NAME WHERE $KEY_ID = :id")
    fun deleteById(id: Long)

    @Update
    fun update(vararg scheduleEntity: ScheduleEntity)

    @Query("UPDATE $TABLE_NAME SET $KEY_IMGS = :joinedImgString WHERE $KEY_ID = :id;")
    fun updateImgs(id: Long, joinedImgString: String)

    @Query("SELECT * FROM $TABLE_NAME order by $KEY_TIME")
    fun getCursorOrderByTime(): Cursor

    @Query("SELECT * FROM $TABLE_NAME order by $KEY_TIME")
    fun getAll(): List<ScheduleEntity>

    @Query("SELECT $KEY_IMGS FROM $TABLE_NAME WHERE $KEY_ID = :id")
    fun getImgs(id: Long): String


    @Query("DELETE FROM $TABLE_NAME")
    fun deleteAll()
}