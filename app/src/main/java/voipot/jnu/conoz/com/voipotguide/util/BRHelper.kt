package voipot.jnu.conoz.com.voipotguide.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.*

class BRHelper(mContext: Context) : IBRHelper {
    private val localBRManager = LocalBroadcastManager.getInstance(mContext)

    private val brList = LinkedList<BroadcastReceiver>()
    override fun registerReceiver(receiver: BroadcastReceiver, intentFilter: IntentFilter) {
        localBRManager.registerReceiver(receiver, intentFilter)
        brList.add(receiver)
    }

    override fun unregisterReceiver(broadcastReceiver: BroadcastReceiver) {
        localBRManager.unregisterReceiver(broadcastReceiver)
        brList.remove(broadcastReceiver)
    }


    override fun unregisterAllReceiver() {
        brList.forEach {
            localBRManager.unregisterReceiver(it)
        }
    }
}