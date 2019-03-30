package voipot.jnu.conoz.com.voipotguide.util

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import voipot.jnu.conoz.com.voipotguide.room.AppDatabase
import voipot.jnu.conoz.com.voipotguide.room.entity.ScheduleEntity
import java.util.*

class ScheduleManager(db: AppDatabase) : IScheduleManager {
    private val scheduleDao = db.scheduleDAO()

    override fun add(entity: ScheduleEntity): Long {
        entity.prepareToUpload()
        return scheduleDao.add(entity)
    }

    override fun delete(id: Long) {
        scheduleDao.deleteById(id)
    }

    override fun update(entity: ScheduleEntity) {
        entity.prepareToUpload()
        scheduleDao.update(entity)
    }

    override fun overwrite(entityList: List<ScheduleEntity>) {
        scheduleDao.deleteAll()
        entityList.forEach {
            it.prepareToUpload()
        }
        scheduleDao.addAll(entityList)
    }

    override fun getAll(): List<ScheduleEntity> {
        return scheduleDao.getAll().apply {
            forEach { entity ->
                entity.initImgList()
            }
        }
    }

    override fun updateImgs(entity: ScheduleEntity) {
        entity.prepareToUpload()
        scheduleDao.updateImgs(entity.id!!, entity.joinedImgString!!)
    }

    override fun getImgs(id: Long): List<String> {
        return scheduleDao.getImgs(id).split(",")
    }

    override fun getPosition(entity: ScheduleEntity): Int {
        val cursor = scheduleDao.getCursorOrderByTime()

        var position = 0
        while (cursor.moveToNext()) {
            if (entity.id == cursor.getLong(0)) {
                cursor.close()
                return position
            } else {
                position++
            }
        }

        //찾을 데이터가 없음
        return -1
    }

}