package voipot.jnu.conoz.com.voipotguide.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "message")
class MessageEntity(
        @PrimaryKey(autoGenerate = true) var id: Int,
        var name: String,
        var time: String,
        var message: String,
        var isRead: Boolean)