package voipot.jnu.conoz.com.voipotguide.room

import androidx.room.TypeConverter

object TimeConverter {
    @TypeConverter
    @JvmStatic
    fun timeToString(time: Time?): String? {
        return time?.toString()
    }

    @TypeConverter
    @JvmStatic
    fun stringToTime(string: String?): Time? {
        return string?.let { Time(it) }
    }
}