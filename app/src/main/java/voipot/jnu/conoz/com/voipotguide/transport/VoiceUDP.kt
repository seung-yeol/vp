package voipot.jnu.conoz.com.voipotguide.transport

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import voipot.jnu.conoz.com.voipotguide.CommonApp
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * Created by seung-yeol on 2018. 7. 19..
 */
class VoiceUDP {
    private val TAG = "VoiceUDP"
    private val SAMPLE_RATE = 8000
    private val SAMPLE_INTERVAL = 20
    private val SAMPLE_SIZE = 2
    private val BUF_SIZE = SAMPLE_INTERVAL * SAMPLE_INTERVAL * SAMPLE_SIZE * 2
    private var enableSend = true

    fun sendVoice() {
        enableSend = true
        val thread = Thread(Runnable {
            val audioRecorder = AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) * 10)

            var bytesRead: Int
            var bytesSent = 0
            val buf = ByteArray(BUF_SIZE)

            audioRecorder.startRecording()

            var strAddress: String?
            val socket = DatagramSocket()

            while (enableSend) {
                // 녹음을 하고 전송
                bytesRead = audioRecorder.read(buf, 0, BUF_SIZE)
                if (CommonApp.clientList.size > 0) {
                    for (i in 0 until CommonApp.clientList.size) {
                        strAddress = CommonApp.clientList[i].ip
                        val address = InetAddress.getByName(strAddress)
                        Log.i(TAG, "[sendVoice] Packet destination: " + address.toString())
                        try {
                            val packet = DatagramPacket(buf, bytesRead, address, CommonApp.PORT_VOICE)
                            socket.send(packet)
                            Thread.sleep(SAMPLE_INTERVAL.toLong(), 0)
                        } catch (e: IllegalArgumentException) {
                            Log.d(TAG, "Error address:$address")
                            e.printStackTrace()
                        }

                    }
                }
                bytesSent += bytesRead
                Log.i(TAG, "[sendVoice] Total bytes sent: $bytesSent")
            }
            // 녹음을 멈추고 자원 해제
            audioRecorder.stop()
            audioRecorder.release()
            socket.disconnect()
            socket.close()
            enableSend = false
        })
        thread.start()
    }

    fun stopSend() {
        enableSend = false
    }
}