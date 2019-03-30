package voipot.jnu.conoz.com.voipotguide.transport

import android.util.Log
import voipot.jnu.conoz.com.voipotguide.CommonApp
import java.net.DatagramPacket
import java.net.DatagramSocket

class ConnCheckUDP{
    private var enableReceiveNetwork = true

    fun recvConn() {
        println("start connCheckUDP")
        Thread(Runnable {
            val packetBuffer = ByteArray(128)
            var socket: DatagramSocket? = null

            try {
                socket = DatagramSocket(CommonApp.PORT_FIND_MEMBER)
                var datagramPacket: DatagramPacket
                while (enableReceiveNetwork) {
                    datagramPacket = DatagramPacket(packetBuffer, 128)
                    socket.receive(datagramPacket)
                    if (datagramPacket.data.isNotEmpty()) {
                        val recvAddress = datagramPacket.address.hostAddress.toString()
                        val recvData = String(datagramPacket.data, Charsets.UTF_8)

                        println("recvData : $recvData")
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                Log.e(this.toString(), "Exception: " + e.message)
            } catch (e: InterruptedException) {
                e.printStackTrace()
                Log.e(this.toString(), "Exception: " + e.message)
            } finally {
                socket?.close()
            }
        }).start()
    }
}