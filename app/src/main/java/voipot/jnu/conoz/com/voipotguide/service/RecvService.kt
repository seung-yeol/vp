package voipot.jnu.conoz.com.voipotguide.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import voipot.jnu.conoz.com.voipotguide.CommonApp
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket

const val ACTION_RECVMESSAGE_RESULT = "ACTION_RECVMESSAGE_RESULT"
const val KEY_MESSAGE_RESULT = "KEY_MESSAGE_RESULT"
const val KEY_ADDRESS_RESULT = "KEY_ADDRESS_RESULT"

const val ACTION_RECVCONNECT_RESULT = "ACTION_RECVCONNECT_RESULT"
const val KEY_CLIENT_NAME = "KEY_CLIENT_NAME"
const val KEY_CLIENT_ADDRESS = "KEY_CLIENT_ADDRESS"

class RecvService : IntentService("RecvService") {
    private var isOn = true
    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_RECVMESSAGE_RESULT -> {
                recvMessage()
            }
            ACTION_RECVCONNECT_RESULT -> {
                recvConnect()
            }
        }
    }

    private fun recvConnect() {
        println("start recvConnect")
        Thread(Runnable {
            val packetBuffer = ByteArray(128)
            var socket: DatagramSocket? = null

            try {
                socket = DatagramSocket(CommonApp.PORT_FIND_MEMBER)
                var datagramPacket: DatagramPacket
                while (isOn) {
                    datagramPacket = DatagramPacket(packetBuffer, 128)
                    socket.receive(datagramPacket)
                    if (datagramPacket.data.isNotEmpty()) {
                        val recvAddress = datagramPacket.address.hostAddress.toString()
                        val recvData = String(datagramPacket.data, Charsets.UTF_8)

                        println("recvData : $recvData")
                        LocalBroadcastManager.getInstance(applicationContext)
                                .sendBroadcast(Intent(ACTION_RECVCONNECT_RESULT).apply {
                                    putExtra(KEY_CLIENT_NAME, recvData)
                                    putExtra(KEY_CLIENT_ADDRESS, recvAddress)
                                })
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

    private fun recvMessage() {
        //핫스팟에 연결된 클라이언트들 정보 얻어옴.
        println("start recvMessage")
        while (isOn) {
            val socket = DatagramSocket(CommonApp.PORT_MESSAGE)
            try {
                while (true) {
                    val packetBuffer = ByteArray(512)
                    val datagramPacket = DatagramPacket(packetBuffer, 512)
                    socket.receive(datagramPacket)

                    if (datagramPacket.data.isNotEmpty()) {
                        val address = datagramPacket.address.hostAddress.toString()

                        //채워지지 않은 배열 지우기
                        for ((i, bb) in datagramPacket.data.withIndex()) {
                            if (bb == 0.toByte()) {
                                ByteArray(i).apply {
                                    for (k in 0..(i - 1)) {
                                        set(k, datagramPacket.data[k])
                                    }
                                }.let {
                                    datagramPacket.data = it
                                }
                                break
                            }
                        }

                        String(datagramPacket.data, Charsets.UTF_8).split("|,").let {
                            println("recvMessage : ${it[1]}")
                            LocalBroadcastManager.getInstance(applicationContext)
                                    .sendBroadcast(Intent(ACTION_RECVMESSAGE_RESULT).apply {
                                        putExtra(KEY_CLIENT_NAME, it[0])
                                        putExtra(KEY_MESSAGE_RESULT, it[1])
                                        putExtra(KEY_ADDRESS_RESULT, address)
                                    })
                        }



                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(this.toString(), "Exception: " + e.message)
            } catch (e: InterruptedException) {
                e.printStackTrace()
                Log.e(this.toString(), "Exception: " + e.message)
            } finally {
                socket.close()
            }
        }
    }

    override fun onDestroy() {
        isOn = false
        super.onDestroy()
    }
}
