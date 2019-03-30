package voipot.jnu.conoz.com.voipotguide.transport

import android.util.Log
import voipot.jnu.conoz.com.voipotguide.CommonApp
import voipot.jnu.conoz.com.voipotguide.vo.MessageVO
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress


class MessageUDP {
    private val TAG = "MessageUDP"
    private var socket: DatagramSocket? = null

    fun sendMessage(clientIP: String, vo: MessageVO) {
        println("clientIP : $clientIP")
        println("message : ${vo.message}")

        Thread(Runnable {
            try {
                val inetAddress = InetAddress.getByName(clientIP)
                val sendData = (vo.name!! + "|," + vo.time + "|," + vo.message!!).toByteArray()
                val packet = DatagramPacket(sendData, sendData.size, inetAddress, CommonApp.PORT_MESSAGE)
                socket = DatagramSocket()

                socket!!.broadcast = true
                socket!!.send(packet)
                socket!!.close()
                Log.d(TAG, "UDP Send " + String(sendData, 0, sendData.size))
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "IOException : " + e.message)
            } finally {
                socket?.close()
            }
        }).start()
    }
}