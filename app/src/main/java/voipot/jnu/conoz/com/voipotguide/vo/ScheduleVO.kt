//package voipot.jnu.conoz.com.voipotguide.vo
//
//class ScheduleVO {
//    var id: Long? = null
//    lateinit var time: String
//    lateinit var title: String
//    var memo: String? = null
//    var imgList: MutableList<String>? = null
//
//    fun getImgsSize(): Int {
//        return if (imgList == null) {
//            0
//        } else {
//            imgList!!.size
//        }
//    }
//
//    fun getJoinedImgString(): String? {
//        return if (imgList != null && imgList!!.isNotEmpty()) {
//            imgList!!.joinToString(",")
//        } else {
//            null
//        }
//    }
//
//    fun setImgListFromJoinedString(imgString: String?) {
//        if (imgString != null) {
//            imgList = ArrayList(imgString.split(","))
//        }
//    }
//}