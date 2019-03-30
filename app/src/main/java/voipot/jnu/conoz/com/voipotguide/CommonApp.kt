package voipot.jnu.conoz.com.voipotguide

import android.annotation.SuppressLint
import android.content.Intent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import voipot.jnu.conoz.com.voipotguide.dagger.component.DaggerAppComponent
import voipot.jnu.conoz.com.voipotguide.service.*
import voipot.jnu.conoz.com.voipotguide.util.DataManager
import voipot.jnu.conoz.com.voipotguide.util.IDataManager
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by seung-yeol on 2018. 7. 19..
 */
class CommonApp : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().context(this).build()
    }

    data class ClientInfo(var name: String, var ip: String, var isConnect: Boolean)

    companion object {
        const val PORT_ALL_RECV = 8894
        const val PORT_FIND_MEMBER = 8888
        const val PORT_VOICE = 8889
        const val PORT_GPS = 8890
        const val PORT_MESSAGE = 8891
        const val PORT_SCHEDULE = 8892
        const val PORT_IMG = 8893

        var travleTitle: String? = null
        var clientList = arrayListOf<ClientInfo>()
        var isNameFilled = false

        fun nameFillCheck(): Boolean {
            for (clientInfo in clientList) {
                if (clientInfo.name.isEmpty()) {
                    isNameFilled = false
                    return false
                } else {
                    println("name : ${clientInfo.name}")
                }
            }
            println("고객 이름 다 작성 완료")
            isNameFilled = true
            return true
        }

        fun clientSetName(name: String, ip: String) {
            for (clientInfo in clientList) {
                println("client ip : ${clientInfo.ip} name : ${clientInfo.name}")
                if (clientInfo.name == name) {
                    return
                } else if (clientInfo.ip == ip) {
                    clientInfo.name = name
                    println("set client ip : ${clientInfo.ip} name : ${clientInfo.name}")
                }
            }
        }

        fun isConnect(ip: String): Boolean {
            for (clientInfo in clientList) {
                if (ip == clientInfo.ip) {
                    return clientInfo.isConnect
                }
            }
            return false
        }

        fun getClientName(ip: String): String? {
            for (clientInfo in clientList) {
                if (clientInfo.ip == ip) {
                    return clientInfo.name
                }
            }

            return null
        }

        @SuppressLint("SimpleDateFormat")
        fun getCurrentTime(): String {
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(System.currentTimeMillis()))
        }

        fun addClient(clientInfo: ClientInfo) {
            if (!clientList.asSequence()
                            .map { info -> info.ip }
                            .contains(clientInfo.ip)) {
                clientList.add(clientInfo)
                isNameFilled = false
            }
        }
    }

    private var connCheckService: Intent? = null
    private var recvMessageService: Intent? = null
    private var recvConnectService: Intent? = null
    private var recvAllService: Intent? = null

    @Inject lateinit var mDataManager: IDataManager

    override fun onCreate() {
        mDataManager = DataManager(this)


//        clientSet?.map {
//            it.split("|,")
//        }?.forEach {
//            clientList.add(ClientInfo(it[0], it[1], false))
//        }

//        clientList
        //고객이름 ip 도 얻어와야함

        super.onCreate()
    }

    override fun onTerminate() {
        connCheckService?.run { stopService(this) }
        recvMessageService?.run { stopService(this) }
        recvConnectService?.run { stopService(this) }
        super.onTerminate()
    }

    fun startIntentService() {
        connCheckService = Intent(this, ConnCheckService::class.java)
        recvMessageService = Intent(this, RecvService::class.java).apply {
            action = ACTION_RECVMESSAGE_RESULT
        }
        recvConnectService = Intent(this, RecvService::class.java).apply {
            action = ACTION_RECVCONNECT_RESULT
        }

        recvAllService = Intent(this, RecvAllService::class.java)

        startService(connCheckService)
        startService(recvMessageService)
        startService(recvConnectService)
        startService(recvAllService)

//        val receiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                ImgSendTCP().send(intent!!.getStringExtra(KEY_IMG_FATH), intent.getStringExtra(KEY_CLIENT_IP))
//            }
//        }
//        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter(ACTION_IMG_SEND))
    }

    fun loginOnOff(isGuide: Boolean) {
        mDataManager.loginOnOff(isGuide)
    }
}