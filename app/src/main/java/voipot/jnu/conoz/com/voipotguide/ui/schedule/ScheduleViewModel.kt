package voipot.jnu.conoz.com.voipotguide.ui.schedule

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject


class ScheduleViewModel @Inject constructor() : ViewModel() {
    private val _addScheduleClick = MutableLiveData<Boolean>()
    val addScheduleClick: LiveData<Boolean>
        get() = _addScheduleClick

    init {

    }

    fun onAddScheduleClick(clickedView: View) {
        _addScheduleClick.value = true
    }
}