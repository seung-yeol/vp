package voipot.jnu.conoz.com.voipotguide.util

import voipot.jnu.conoz.com.voipotguide.room.entity.ScheduleEntity

interface IScheduleManager {
    fun add(entity: ScheduleEntity): Long
    fun delete(id: Long)
    fun update(entity: ScheduleEntity)
    fun overwrite(entityList: List<ScheduleEntity>)
    fun updateImgs(entity: ScheduleEntity)
    fun getAll(): List<ScheduleEntity>
    fun getImgs(id: Long): List<String>
    fun getPosition(entity: ScheduleEntity): Int
}