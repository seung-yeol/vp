package voipot.jnu.conoz.com.voipotguide.util

import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager

class HotspotUtil(private val wifiManager: WifiManager){
    fun hotspotCheckStart(hotspotOnListener : () -> Unit) {
        Thread(Runnable {
            while (true) {
                Thread.sleep(500)
                println("핫스팟 켜지기를 대기중")
                if (isApOn()) {
                    println("핫스팟 켜짐!!!!!!!!!!!!")
                    hotspotOnListener()
                    break
                }
            }
        }).start()
    }

    fun isApOn(): Boolean {
        try {
            val method = wifiManager.javaClass.getDeclaredMethod("isWifiApEnabled")
            method.isAccessible = true
            return method.invoke(wifiManager) as Boolean
        } catch (ignored: Throwable) {
            ignored.stackTrace
        }

        return false
    }

    //롤리팝 버전 미만인 경 우
    fun hotspotOnOff(): Boolean {
        val wifiConf: WifiConfiguration? = null
        try {
            // if WiFi is on, turn it off
            if (isApOn()) {
                wifiManager.isWifiEnabled = false
            }
            val method = wifiManager.javaClass.getMethod("setWifiApEnabled", WifiConfiguration::class.java, Boolean::class.javaPrimitiveType)
            method.invoke(wifiManager, wifiConf, !isApOn())
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }
}