package voipot.jnu.conoz.com.voipotguide.transport

import voipot.jnu.conoz.com.voipotguide.CommonApp
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class ImgUDP {
    fun send(filePath: String, address: String) {
        val f = File(filePath)

        if (!f.exists()) {
            return
        }

        Thread(Runnable {
            Thread.sleep(100)
            val datagramSocket: DatagramSocket?

            try {
                datagramSocket = DatagramSocket()
                val clientAddress = InetAddress.getByName(address)
                var str = "filename=${f.name}"
                var datagramPacket = DatagramPacket(str.toByteArray(), str.toByteArray().size, clientAddress, CommonApp.PORT_IMG)
                datagramSocket.send(datagramPacket)

                val fileStream = BufferedInputStream(FileInputStream(f), 1024)

                System.out.printf("file name = $str")

                val buffer = ByteArray(512)
                var count = 0
                while (true) {
                    Thread.sleep(10)

                    val readByte = fileStream.read(buffer, 0, buffer.size)
                    if (readByte == -1)
                        break
                    datagramPacket = DatagramPacket(buffer, readByte, clientAddress, CommonApp.PORT_IMG) // *
                    datagramSocket.send(datagramPacket)
                    println(readByte)

                    count++
                }
                str = "end"
                datagramPacket = DatagramPacket(str.toByteArray(), str.toByteArray().size, clientAddress, CommonApp.PORT_IMG)
                datagramSocket.send(datagramPacket)

                fileStream.close()
                datagramSocket.close()

            } catch (e: Exception) {
                println(e.message)
            }
        })
    }
}