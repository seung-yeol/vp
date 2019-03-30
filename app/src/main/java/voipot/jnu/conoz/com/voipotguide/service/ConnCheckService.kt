package voipot.jnu.conoz.com.voipotguide.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import voipot.jnu.conoz.com.voipotguide.CommonApp
import voipot.jnu.conoz.com.voipotguide.util.HotspotUtil
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.net.InetAddress

const val ACTION_AP_ON_RESULT = "ACTION_AP_ON_RESULT"
const val KEY_IS_AP_ON = "KEY_IS_AP_ON"

class ConnCheckService : IntentService("ConnCheckService") {
    private var isOn = true
    override fun onHandleIntent(intent: Intent?) {
        //핫스팟에 연결된 클라이언트들 정보 얻어옴.
        while (isOn) {
            println("핫스팟 새로 검색")
            var br: BufferedReader? = null

            try {
                br = BufferedReader(FileReader("/proc/net/arp"))
                var line = br.readLine()
                while (line != null) {
                    val splitted = line.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                    if (splitted.size >= 4) {
                        val mac = splitted[3]

                        if (mac.matches("..:..:..:..:..:..".toRegex())) {
                            val isReachable = InetAddress.getByName(splitted[0]).isReachable(300)
                            CommonApp.addClient(CommonApp.ClientInfo("", splitted[0], isReachable))
                            if (isReachable) {
                                println("wifi 접속 중인 ip : ${splitted[0]}")
                            } else {
                                println("wifi 접속 끊긴 ip : ${splitted[0]}")
                            }
                        }
                    }
                    line = br.readLine()
                }
            } catch (e: Exception) {
                System.err.println(e.toString())
            } finally {
                try {
                    br!!.close()
                } catch (e: IOException) {
                    System.err.println(e.toString())
                }
            }

            val hu = HotspotUtil(applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)

            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(ACTION_AP_ON_RESULT).apply {
                if (!hu.isApOn()) {
                    putExtra(KEY_IS_AP_ON, false)
                } else {
                    putExtra(KEY_IS_AP_ON, true)
                }
            })


            Thread.sleep(5000)
        }
    }

    override fun onDestroy() {
        isOn = false
        super.onDestroy()
    }
}
