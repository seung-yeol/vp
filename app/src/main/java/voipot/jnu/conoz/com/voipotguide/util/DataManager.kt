package voipot.jnu.conoz.com.voipotguide.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import androidx.room.Room
import voipot.jnu.conoz.com.voipotguide.room.AppDatabase
import voipot.jnu.conoz.com.voipotguide.room.entity.ScheduleEntity
import javax.inject.Inject

class DataManager @Inject constructor(context: Context) : IDataManager, IBRHelper, IPreferenceHelper, IScheduleManager {
    private val brHelper: IBRHelper = BRHelper(context)

    private val preferenceHelper: IPreferenceHelper = PreferenceHelper(context)

    private val db = Room.databaseBuilder(context.applicationContext,
            AppDatabase::class.java, "app_database.db").build()

    private val scheduleManager: IScheduleManager = ScheduleManager(db)

    override val isLoggedIn: LiveData<Boolean>
        get() = preferenceHelper.isLoggedIn

    override fun loginOnOff(isLoggedIn: Boolean) {
        preferenceHelper.loginOnOff(isLoggedIn)
    }

    override fun registerReceiver(broadcastReceiver: BroadcastReceiver, intentFilter: IntentFilter) {
        brHelper.registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun unregisterReceiver(broadcastReceiver: BroadcastReceiver) {
        brHelper.unregisterReceiver(broadcastReceiver)
    }

    override fun unregisterAllReceiver() {
        brHelper.unregisterAllReceiver()
    }

    //schedule
    override fun add(entity: ScheduleEntity): Long {
        return scheduleManager.add(entity)
    }

    override fun getAll(): List<ScheduleEntity> {
        return scheduleManager.getAll()
    }

    override fun delete(id: Long) {
        scheduleManager.delete(id)
    }

    override fun update(entity: ScheduleEntity) {
        scheduleManager.update(entity)
    }

    override fun overwrite(entityList: List<ScheduleEntity>) {
        scheduleManager.overwrite(entityList)
    }

    override fun updateImgs(entity: ScheduleEntity){
        scheduleManager.updateImgs(entity)
    }

    override fun getImgs(id: Long): List<String> {
        return scheduleManager.getImgs(id)
    }

    override fun getPosition(entity: ScheduleEntity): Int {
        return scheduleManager.getPosition(entity)
    }
}
