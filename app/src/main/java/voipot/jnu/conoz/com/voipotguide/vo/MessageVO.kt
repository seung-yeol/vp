package voipot.jnu.conoz.com.voipotguide.vo

class MessageVO {
    var id : Int? = null
    var name: String? = null
    var time: String? = null
    var message: String? = null
    var isRead : Boolean? = null
    var isValid: Boolean = false
        get() = !(name.isNullOrEmpty() || time.isNullOrEmpty() || message.isNullOrEmpty())
}