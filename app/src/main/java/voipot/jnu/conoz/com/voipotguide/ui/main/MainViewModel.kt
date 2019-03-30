package voipot.jnu.conoz.com.voipotguide.ui.main

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import voipot.jnu.conoz.com.voipotguide.R
import voipot.jnu.conoz.com.voipotguide.transport.VoiceUDP
import voipot.jnu.conoz.com.voipotguide.util.IDataManager
import javax.inject.Inject

class MainViewModel @Inject constructor(private val mDataManager: IDataManager) : ViewModel() {
    private var isTalk = false

    private val _isLoggedIn: LiveData<Boolean>
    val isLoggedIn: LiveData<Boolean>
        get() = _isLoggedIn

    private val _isConnect = MutableLiveData<Boolean>()
    val isConnect: LiveData<Boolean>
        get() = _isConnect

    private val _tryLogout = MutableLiveData<Boolean>()
    val tryLogout: LiveData<Boolean>
        get() = _tryLogout

    private val _voiceBtnImg = MutableLiveData<Int>()
    val voiceBtnImg: LiveData<Int>
        get() = _voiceBtnImg

    init {
        _isConnect.value = false
        _tryLogout.value = false
        _voiceBtnImg.value = R.drawable.ic_light_sound

        _isLoggedIn = mDataManager.isLoggedIn
    }

    fun onVoiceBtnClick(clickedView: View) {
        val voiceSender = VoiceUDP()

        isTalk = if (isTalk) {
            voiceSender.stopSend()
            _voiceBtnImg.value = R.drawable.ic_light_sound
            false
        } else {
            voiceSender.sendVoice()
            _voiceBtnImg.value = R.drawable.ic_sound
            true
        }
    }

    fun onLogoutClick(clickedView: View) {
        _tryLogout.value = true
        mDataManager.loginOnOff(false)

//        messageReceiver?.let { receiver -> LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver) }
//        connectReceiver?.let { receiver -> LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver) }
    }
}