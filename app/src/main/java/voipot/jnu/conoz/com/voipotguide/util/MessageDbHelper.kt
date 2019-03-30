package voipot.jnu.conoz.com.voipotguide.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import voipot.jnu.conoz.com.voipotguide.vo.MessageVO

class MessageDbHelper(context: Context) : SQLiteOpenHelper(context, "AppDatabase", null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_VERSION: Int = 1
        const val KEY_ID = "ID"
        const val KEY_NAME = "NAME"
        const val KEY_TIME = "TIME"
        const val KEY_MESSAGE = "MESSAGE"
        const val KEY_ISREAD = "ISREAD"
        const val TABLE_NAME = "Message"
        const val LIMIT_NUM = 50
        private var INSTANCE : MessageDbHelper? = null

        fun getInstance(context: Context) : MessageDbHelper {
            return if (INSTANCE == null) {
                MessageDbHelper(context)
            } else{
                INSTANCE!!
            }
        }
    }

    private val mDb : SQLiteDatabase by lazy {
        writableDatabase
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_NAME + " TEXT NOT NULL," +
                KEY_TIME + " NUMERIC NOT NULL," +
                KEY_MESSAGE + " TEXT NOT NULL," +
                KEY_ISREAD + " INTEGER NOT NULL" +
                ");"
        db!!.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun add(messageVO: MessageVO) {
        if (messageVO.isValid) {
            println("메시지 : ${messageVO.message}")
            val isRead = if (messageVO.isRead != null && messageVO.isRead!!) 1 else 0
            val query = "INSERT INTO $TABLE_NAME ($KEY_NAME, $KEY_TIME, $KEY_MESSAGE, $KEY_ISREAD) " +
                    "values('${messageVO.name}', '${messageVO.time}', '${messageVO.message}', $isRead);"
            mDb.execSQL(query)
        } else {
            println("이름 : ${messageVO.name}")
            println("메시지 : ${messageVO.message}")
            println("시간 : ${messageVO.time}")
//            throw RuntimeException("messageVO에 데이터 덜 채워졌어요,,")
        }
    }

    fun getMessages(page: Int) : ArrayList<MessageVO>{
        val s = mDb.compileStatement("select count(*) from $TABLE_NAME; ")
        val minimumId = s.simpleQueryForLong() - page * LIMIT_NUM
        val maximumId = minimumId + LIMIT_NUM

        println("minimumId : $minimumId")
        println("maximumId : $maximumId")

        val query = "select * from $TABLE_NAME where $KEY_ID > $minimumId and $KEY_ID <= $maximumId order by $KEY_ID desc;"
        val cursor = mDb.rawQuery(query, null)

        println("읽어올 메시지 수 : ${cursor.count}")

        val messageList = arrayListOf<MessageVO>()
        while (cursor.moveToNext()) {
            val messageVO = MessageVO()
            messageVO.id = cursor.getInt(0)
            messageVO.name = cursor.getString(1)
            messageVO.time = cursor.getString(2)
            messageVO.message = cursor.getString(3)
            messageVO.isRead = cursor.getInt(4) == 1
            println(messageVO.message)
            messageList.add(messageVO)
        }
        cursor.close()

        readUpdate()

        return messageList
    }

    private fun readUpdate() {
        val query = "UPDATE $TABLE_NAME SET $KEY_ISREAD = 1 WHERE $KEY_ISREAD = 0;"
        mDb.execSQL(query)
    }
}