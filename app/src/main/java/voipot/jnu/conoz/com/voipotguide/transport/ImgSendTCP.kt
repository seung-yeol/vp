package voipot.jnu.conoz.com.voipotguide.transport

import java.io.*
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException

class ImgSendTCP {
    fun send(filePath: String, clientIP: String) {
        Thread.sleep(100)
        Thread(Runnable {
            println("img send")
            val file = File(filePath)
            if (!file.exists()) {
                println("File not Exist.")
                return@Runnable
            }

            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var readBytes: Int

            val inetAddress = InetAddress.getByName(clientIP)
            val socket = Socket(inetAddress, 8899)
            val fis = FileInputStream(file)
            val os = socket.getOutputStream()

            try {
                if (!socket.isConnected) {
                    println("Socket Connect Error.")
                }

                val out = PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())), true)
                out.println("imgSize=${file.length()}")
                println("imgSize=${file.length()}")

                do{
                    readBytes = fis.read(buffer)
                    os.write(buffer, 0, buffer.size)
                }
                while (readBytes > 0)
                println("send complete")

                BufferedReader(InputStreamReader(socket.getInputStream())).let {
                    val returnedMsg = it.readLine()
                    println("img send returnedMsg : $returnedMsg")
                }

            } catch (e: UnknownHostException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                fis.close()
                os.close()
                socket.close()
            }
        }).start()
    }
}