package voipot.jnu.conoz.com.voipotguide.util

import android.content.BroadcastReceiver
import android.content.IntentFilter

interface IBRHelper {
    fun registerReceiver(broadcastReceiver: BroadcastReceiver, intentFilter: IntentFilter)
    fun unregisterReceiver(broadcastReceiver: BroadcastReceiver)
    fun unregisterAllReceiver()
}