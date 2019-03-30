package voipot.jnu.conoz.com.voipotguide.transport

import android.util.Log
import voipot.jnu.conoz.com.voipotguide.CommonApp
import voipot.jnu.conoz.com.voipotguide.room.entity.ScheduleEntity
import java.io.*
import java.net.InetAddress
import java.net.Socket

class ScheduleSendTCP {
    private val TAG = "ScheduleSendTCP"
    private var recvOn = true
//    private var socket: DatagramSocket? = null

    fun sendSchedule(clientIP: String, entity: ScheduleEntity) {
        println("clientIP : $clientIP")
        println("title : ${entity.title}")
        println("memo : ${entity.memo}")
        println("time : ${entity.time}")
        println("imgCount : ${entity.imgCount}")

        Thread(Runnable {
            try {
                val inetAddress = InetAddress.getByName(clientIP)
                val socket = Socket(inetAddress, CommonApp.PORT_SCHEDULE)

                try {
                    val out = PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)
                    out.println("${entity.id!!}|," + entity.time + "|," + entity.title + "|," + entity.memo + "|," + entity.imgCount)

                    BufferedReader(InputStreamReader(socket.getInputStream())).let {
                        val returnedMsg = it.readLine()
                        println("returnedMsg : $returnedMsg")
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    Log.e(TAG, "IOException : " + e.message)
                } finally {
                    socket.close()
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                Log.e(TAG, "IOException : " + e.message)
            }
        }).start()
    }
}