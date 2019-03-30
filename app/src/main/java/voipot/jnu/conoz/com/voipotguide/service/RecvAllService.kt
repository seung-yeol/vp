package voipot.jnu.conoz.com.voipotguide.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import voipot.jnu.conoz.com.voipotguide.CommonApp
import voipot.jnu.conoz.com.voipotguide.transport.ImgSendTCP
import java.io.*
import java.net.ServerSocket

const val ACTION_IMG_SEND = "ACTION_IMG_SEND"
const val KEY_IMG_FATH = "KEY_IMG_FATH"
const val KEY_CLIENT_IP = "KEY_CLIENT_IP"

class RecvAllService : IntentService("RecvAllService") {
    private var isOn = true
    override fun onHandleIntent(intent: Intent?) {
        var serverSocket: ServerSocket? = null
        var dataManager = (application as CommonApp).mDataManager

        try {
            serverSocket = ServerSocket(8888)
            while (isOn) {
                val socket = serverSocket.accept()

                try {
                    val bufferReader = BufferedReader(InputStreamReader(socket.getInputStream()))
                    val readLine = bufferReader.readLine()
                    println("client ip = ${socket.inetAddress.hostAddress}")

                    if (readLine.isNotEmpty()) {
                        val splitData = readLine.split("|,")

                        when (splitData[0]) {
                            "request img" -> {
                                val imgPath = dataManager.getImgs(splitData[1].toLong())[splitData[2].toInt()]

                                val printWriter = PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)

                                imgPath.split(".").last().let {
                                    printWriter.println("send:$it")
                                    printWriter.flush()
                                }
                                if (BufferedReader(InputStreamReader(socket.getInputStream())).readLine() == "start"){
                                    println("가즈아")
                                    ImgSendTCP().send(imgPath, socket.inetAddress.hostAddress)
                                }
                            }
                        }
                    }
                } catch (t: Throwable) {
                    t.printStackTrace()
                    Log.e(this.toString(), "Exception: " + t.message)
                } finally {
                    socket.close()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(this.toString(), "Exception: " + e.message)
        } catch (e: InterruptedException) {
            e.printStackTrace()
            Log.e(this.toString(), "Exception: " + e.message)
        } finally {
            serverSocket?.close()
        }
    }

    override fun onDestroy() {
        isOn = false
        super.onDestroy()
    }
}
