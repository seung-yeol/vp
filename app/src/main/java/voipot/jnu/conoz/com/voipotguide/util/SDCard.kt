package voipot.jnu.conoz.com.voipotguide.util

import java.util.*

//http://indienote.tistory.com/31에서 퍼옴
object SDCard {
    val externalSDCardPath: String?
        get() {
            val hs = externalMounts
            for (extSDCardPath in hs) {
                return extSDCardPath
            }
            return null
        }

    //String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
    // parse output
    private val externalMounts: HashSet<String>
        get() {
            val out = HashSet<String>()
            val reg = "(?i).*media_rw.*(storage).*(sdcardfs).*rw.*"
            var s = ""
            try {
                val process = ProcessBuilder().command("mount").redirectErrorStream(true).start()
                process.waitFor()
                val `is` = process.inputStream
                val buffer = ByteArray(1024)
                while (`is`.read(buffer) !== -1) {
                    s += String(buffer)
                }
                `is`.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val lines = s.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (line in lines) {
                if (!line.toLowerCase(Locale.US).contains("asec")) {
                    if (line.matches(reg.toRegex())) {
                        val parts = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        for (part in parts) {
                            if (part.startsWith("/")) {
                                if (!part.toLowerCase(Locale.US).contains("vold") && !part.toLowerCase(Locale.US).contains("/mnt/")) {
                                    out.add(part)
                                }
                            }
                        }
                    }
                }
            }

            return out
        }
}