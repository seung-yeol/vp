package voipot.jnu.conoz.com.voipotguide.util

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

interface IPreferenceHelper{
    val isLoggedIn: LiveData<Boolean>
    fun loginOnOff(isLoggedIn: Boolean)
}