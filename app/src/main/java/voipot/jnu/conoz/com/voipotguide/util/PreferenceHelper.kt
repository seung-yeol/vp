package voipot.jnu.conoz.com.voipotguide.util

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import voipot.jnu.conoz.com.voipotguide.CommonApp

class PreferenceHelper(mContext: Context): IPreferenceHelper {
    private var _isLoggedIn = MutableLiveData<Boolean>()
    override val isLoggedIn: LiveData<Boolean>
        get() = _isLoggedIn

    private val mSharedPreference: SharedPreferences = mContext.getSharedPreferences("setting", Context.MODE_PRIVATE)
    private var clientSet: MutableSet<String>?
    private var clientList = arrayListOf<CommonApp.ClientInfo>()

    init {
        mSharedPreference.run {
            _isLoggedIn.value = getBoolean("isLoggedIn", false)
            clientSet = getStringSet("clientSet", null)
        }
    }

    override fun loginOnOff(isLoggedIn: Boolean) {
        this._isLoggedIn.value = isLoggedIn

        val clientSet = mutableSetOf<String>()

        clientList.forEach {
            clientSet.add(it.ip + "|," + it.name)
        }

        mSharedPreference.edit().apply {
            putBoolean("isLoggedIn", isLoggedIn)
            putStringSet("clientSet", clientSet)
        }.apply()
    }
}
