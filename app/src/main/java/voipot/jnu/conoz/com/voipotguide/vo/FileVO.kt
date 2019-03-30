package voipot.jnu.conoz.com.voipotguide.vo

import java.io.File

class FileVO{
    var name : String? = null
    var nextPath : String? = null
    var parentPath : String? = null
    var count : Int? = null
    var isFile : Boolean = false
    var isBack : Boolean = false
    var childFiles : Array<File>? = null
}