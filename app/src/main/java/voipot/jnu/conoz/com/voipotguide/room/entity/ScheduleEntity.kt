package voipot.jnu.conoz.com.voipotguide.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import voipot.jnu.conoz.com.voipotguide.room.Time

const val KEY_ID = "id"
const val KEY_TIME = "time"
const val KEY_TITLE = "title"
const val KEY_MEMO = "memo"
const val KEY_IMGS = "imgList"
const val TABLE_NAME = "schedule"

@Entity(tableName = TABLE_NAME)
class ScheduleEntity(
        @PrimaryKey(autoGenerate = true) var id: Long?,
        var time: Time?,
        var title: String,
        var memo: String?,
        @ColumnInfo(name = KEY_IMGS) var joinedImgString: String?,
        var imgCount: Int) {
    constructor() : this(null, null, "", null, null, 0)

    @Ignore
    val imgList: MutableList<String> = ArrayList()

    fun initImgList() {
        if (!joinedImgString.isNullOrEmpty()) {
            imgList.addAll(joinedImgString!!.split(",|"))

        }
    }

    fun prepareToUpload() {
        joinedImgString = imgList.joinToString(",|")
        imgCount = imgList.size
    }
}