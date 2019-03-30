package voipot.jnu.conoz.com.voipotguide.service

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager

const val ACTION_HOTSPOT_ON = "ACTION_HOTSPOT_ON"
const val ACTION_HOTSPOT_OFF = "ACTION_HOTSPOT_OFF"
const val KEY_HOTSPOT_SSID = "KEY_HOTSPOT_SSID"
const val KEY_HOTSPOT_PW = "KEY_HOTSPOT_PW"

class MyHotspotService : IntentService("HotSpotService") {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_HOTSPOT_ON -> {
                handleActionHotspotOn()
            }
            ACTION_HOTSPOT_OFF -> {
                handleActionBaz()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun handleActionHotspotOn() {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        wifiManager.startLocalOnlyHotspot(object : WifiManager.LocalOnlyHotspotCallback() {
            override fun onStarted(reservation: WifiManager.LocalOnlyHotspotReservation) {
                super.onStarted(reservation)
                val ssid = reservation.wifiConfiguration.SSID
                val pw = reservation.wifiConfiguration.preSharedKey

                val resultIntent = Intent(ACTION_HOTSPOT_ON)
                resultIntent.putExtra(KEY_HOTSPOT_SSID, ssid)
                resultIntent.putExtra(KEY_HOTSPOT_PW, pw)

                LocalBroadcastManager.getInstance(this@MyHotspotService).sendBroadcast(resultIntent)
//                mReservation = reservation
            }

            override fun onStopped() {
                super.onStopped()
                Log.e("hotspot", "onStopped: ")
            }

            override fun onFailed(reason: Int) {
                super.onFailed(reason)
                Log.e("hotspot", "onFailed: ")
            }
        }, null)
    }

    private fun handleActionBaz() {
        TODO("Handle action Baz")
    }

    companion object {
        @JvmStatic
        fun startActionHotspotOn(context: Context) {
            val intent = Intent(context, MyHotspotService::class.java).apply {
                action = ACTION_HOTSPOT_ON
            }
            context.startService(intent)
        }

        @JvmStatic
        fun startActionBaz(context: Context, param1: String, param2: String) {
            val intent = Intent(context, MyHotspotService::class.java).apply {
                action = ACTION_HOTSPOT_OFF
            }
            context.startService(intent)
        }
    }
}
