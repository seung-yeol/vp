package voipot.jnu.conoz.com.voipotguide.transport

import android.util.Log
import voipot.jnu.conoz.com.voipotguide.CommonApp
import java.net.DatagramPacket
import java.net.DatagramSocket

class GpsUDP(private val listener: ClientDistanceChangeListener) {
    data class LatLngVO(var name: String, var address: String, var latitude: Double, var longitude: Double)
    interface ClientDistanceChangeListener {
        fun onClientDistanceChange(latLngVO: LatLngVO)
    }

    private var enableReceiveNetwork = true

    fun recvClientLatLng() {
        println("start gpsUDP")
        Thread(Runnable {
            val packetBuffer = ByteArray(1024)
            var socket: DatagramSocket? = null

            try {
                socket = DatagramSocket(CommonApp.PORT_GPS)
                var datagramPacket: DatagramPacket
                while (enableReceiveNetwork) {
                    datagramPacket = DatagramPacket(packetBuffer, 1024)
                    socket.receive(datagramPacket)
                    if (datagramPacket.data.isNotEmpty()) {
                        val recvAddress = datagramPacket.address.hostAddress.toString()
//                        val recvData = datagramPacket.data.joinToString("") { "${it.toChar()}" }
                        val recvData = String(datagramPacket.data, Charsets.UTF_8)
                        val splitted = recvData.split(",")
                        val latLngVO = LatLngVO(splitted[0], recvAddress, splitted[1].toDouble(), splitted[2].toDouble())

                        println("recvData : $recvData")
                        listener.onClientDistanceChange(latLngVO)
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