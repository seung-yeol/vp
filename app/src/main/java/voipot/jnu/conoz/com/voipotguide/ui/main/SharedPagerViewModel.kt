package voipot.jnu.conoz.com.voipotguide.ui.main

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.viewpager.widget.ViewPager
import voipot.jnu.conoz.com.voipotguide.util.IDataManager
import javax.inject.Inject

class SharedPagerViewModel @Inject constructor(val mDataManager: IDataManager) : ViewModel() {
    val isLoggedIn: LiveData<Boolean>
        get() = mDataManager.isLoggedIn

    private val _isModifyingSchedule = MutableLiveData<Boolean>()
    val isModifyingSchedule: LiveData<Boolean>
        get() = _isModifyingSchedule

    private val _isExpand = MutableLiveData<Boolean>()
    val isExpand: LiveData<Boolean>
        get() = _isExpand

    private val _leftTab = MutableLiveData<String>()
    val leftTab: LiveData<String>
        get() = _leftTab

    private val _rightTab = MutableLiveData<String>()
    val rightTab: LiveData<String>
        get() = _rightTab

    val currentPage = MutableLiveData<Int>()

    init {
        _isExpand.value = false
        _isModifyingSchedule.value = false

        if (isLoggedIn.value!!) {
            _leftTab.value = "Guide"
            _rightTab.value = "Schedule"
        } else {
            _leftTab.value = "Guide"
            _rightTab.value = "User"
            currentPage.value = 0
        }
    }

    val onPageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            if (isLoggedIn.value!!) {
                when (position) {
                    0 -> {
                        _leftTab.value = "Guide"
                        _rightTab.value = "Schedule"
                    }
                    1 -> {
                        _leftTab.value = "Message"
                        _rightTab.value = ""
                    }
                }
            } else {
                when (position) {
                    0 -> {
                        _leftTab.value = "Guide"
                        _rightTab.value = "User"
                    }
                    1 -> {
                        _leftTab.value = "Guide"
                        _rightTab.value = "Schedule"
                    }
                }
            }
        }
    }

    fun onExpandClick(clickedView: View) {
        _isExpand.value = !isExpand.value!!
    }

    fun modifyingModeOnOff(clickedView: View) {
        _isModifyingSchedule.value = !_isModifyingSchedule.value!!
    }

    fun setExpand(b: Boolean) {
        _isExpand.value = b
    }
}