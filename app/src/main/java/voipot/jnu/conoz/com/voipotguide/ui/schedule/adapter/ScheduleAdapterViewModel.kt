package voipot.jnu.conoz.com.voipotguide.ui.schedule.adapter

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import voipot.jnu.conoz.com.voipotguide.CommonApp
import voipot.jnu.conoz.com.voipotguide.room.Time
import voipot.jnu.conoz.com.voipotguide.room.entity.ScheduleEntity
import voipot.jnu.conoz.com.voipotguide.transport.ScheduleSendTCP
import voipot.jnu.conoz.com.voipotguide.util.IDataManager
import voipot.jnu.conoz.com.voipotguide.util.IScheduleManager
import javax.inject.Inject

class ScheduleAdapterViewModel @Inject constructor(private val mDataManager: IDataManager) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val _scheduleData = MutableLiveData<MutableList<ScheduleEntity>>()
    val scheduleData: LiveData<MutableList<ScheduleEntity>>
        get() = _scheduleData

    private val _hasSchedule = MediatorLiveData<Boolean>()
    val hasSchedule: LiveData<Boolean>
        get() = _hasSchedule

    //2way binding해야되서 set도 필요함
    val titleText = MutableLiveData<String>()
    val memoText = MutableLiveData<String>()

    private val _doneClick = MutableLiveData<Boolean>()
    val doneClick: LiveData<Boolean>
        get() = _doneClick

    init {
        _hasSchedule.addSource(_scheduleData){
            _hasSchedule.value = it.size != 0
        }
        _scheduleData.value = ArrayList()

        Observable.just(mDataManager as IScheduleManager)
                .subscribeOn(Schedulers.io())
                .map {
                    it.getAll()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.isNotEmpty()) {
                        changeScheduleList(it)
                    }
                }.also {
                    compositeDisposable.add(it)
                }
    }

    fun removeSchedule(position: Int) {
        _scheduleData.value = _scheduleData.value!!.apply { removeAt(position) }
    }

    fun removeSchedule(item: ScheduleEntity) {
        _scheduleData.value = _scheduleData.value!!.apply { remove(item) }
    }

    fun addSchedule(position: Int, schedule: ScheduleEntity) {
        _scheduleData.value = _scheduleData.value!!.apply { add(position, schedule) }
    }

    fun changeScheduleList(scheduleList: List<ScheduleEntity>) {
        _scheduleData.value = ArrayList(scheduleList)
    }

    fun onAddScheduleClick(clickedView: View) {
        _doneClick.value = true

        val entity = ScheduleEntity().apply {
            time = Time(0, 0)
            title = titleText.value!!
            memo = memoText.value!!
        }

        titleText.value = ""
        memoText.value = ""

        Observable.just(mDataManager)
                .subscribeOn(Schedulers.io())
                .map {
                    entity.id = it.add(entity)
                    mDataManager.getPosition(entity)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { position ->
                    addSchedule(position, entity)
                    /*CommonApp.clientList.forEach { info ->
                        ScheduleSendTCP().sendSchedule(info.ip, entity)
                    }*/
                }.also {
                    compositeDisposable.add(it)
                }
    }

    override fun onCleared() {
        super.onCleared()

        compositeDisposable.clear()
    }
}