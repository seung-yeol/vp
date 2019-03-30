package voipot.jnu.conoz.com.voipotguide.room

class Time {
    var hour = 0
        set(value) {
            if (value > 24 || value < 0) {
                RuntimeException("시가 이상한데?")
            }
            field = value
        }
    var minute = 0
        set(value) {
            if (value > 60 || value < 0) {
                RuntimeException("분이 이상한데?")
            }
            field = value
        }

    constructor()
    constructor(hour: Int, minute: Int) {
        this.hour = hour
        this.minute = minute
    }
    constructor(time: String) {
        time.split(":").apply {
            hour = this[0].toInt()
            minute = this[1].toInt()
        }
    }

    override fun toString(): String {
        return StringBuilder().append(hour).append(":").append(minute).toString()
    }
}