package voipot.jnu.conoz.com.voipotguide.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import voipot.jnu.conoz.com.voipotguide.room.dao.MessageDAO
import voipot.jnu.conoz.com.voipotguide.room.dao.ScheduleDAO
import voipot.jnu.conoz.com.voipotguide.room.entity.MessageEntity
import voipot.jnu.conoz.com.voipotguide.room.entity.ScheduleEntity

@Database(version = 1, entities = [MessageEntity::class, ScheduleEntity::class],exportSchema = false)
@TypeConverters(TimeConverter::class)
abstract class AppDatabase: RoomDatabase(){
    abstract fun messageDAO(): MessageDAO
    abstract fun scheduleDAO(): ScheduleDAO
}