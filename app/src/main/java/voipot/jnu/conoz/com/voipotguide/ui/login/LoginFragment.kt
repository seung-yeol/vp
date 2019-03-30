package voipot.jnu.conoz.com.voipotguide.ui.login


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import voipot.jnu.conoz.com.voipotguide.R
import voipot.jnu.conoz.com.voipotguide.databinding.FragmentLoginBinding
import voipot.jnu.conoz.com.voipotguide.ui.base.BaseFragment
import voipot.jnu.conoz.com.voipotguide.service.ACTION_HOTSPOT_ON
import voipot.jnu.conoz.com.voipotguide.service.KEY_HOTSPOT_PW
import voipot.jnu.conoz.com.voipotguide.service.KEY_HOTSPOT_SSID
import voipot.jnu.conoz.com.voipotguide.util.HotspotUtil
import javax.inject.Inject
import javax.inject.Named

class LoginFragment : BaseFragment<FragmentLoginBinding>(), LoginContract.View {
    private var onTripStartListener: OnTripStartListener? = null
    private val wifiManager: WifiManager by lazy {
        activity!!.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }
    private val hotspotUtil: HotspotUtil by lazy {
        HotspotUtil(wifiManager)
    }
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent!!.action

            if (action == ACTION_HOTSPOT_ON) {
                onTripStartListener!!.onTripStart()
                val ssid = intent.getStringExtra(KEY_HOTSPOT_SSID)
                val pw = intent.getStringExtra(KEY_HOTSPOT_PW)

                println("ssid $ssid")
                println("pw $pw")

                activity?.run { toast("HotSpot On") }
            }
        }
    }

    @Inject
    @Named("login")
    lateinit var vmProviderFactory: ViewModelProvider.Factory

    companion object {

        @JvmStatic
        fun newInstance() =
                LoginFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }

    override val layoutResourceId: Int = R.layout.fragment_login

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        ViewModelProviders.of(this, vmProviderFactory)[LoginViewModel::class.java]

        btnTripStart.setOnClickListener { _ ->
            if (!hotspotUtil.isApOn()) {
                hotspotUtil.hotspotCheckStart {
                    activity!!.runOnUiThread {
                        showNewTravelDialog()
                    }
                }
                toast("핫스팟을 켜주세요!!")

                if (btnTripStart.text == resources.getText(R.string.loginBtnText)) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        hotspotUtil.hotspotOnOff()
                    } else {
                        //버전이 오레오 이상인 경우 직접 핫스팟을 키도록 유도
                        btnTripStart.text = "핫스팟 키러가기!!"
                    }
                } else {
                    val intent = Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(intent)
                }
            } else {
                showNewTravelDialog()
            }
        }
    }

    override fun onAttach(context: Context) {
        if (context is OnTripStartListener)
            onTripStartListener = context
        else
            throw RuntimeException("OnTripStartListener 구현하세요..")
        super.onAttach(context)
    }

    private fun showNewTravelDialog() {
        onTripStartListener!!.onTripStart()
    }

    //LocalOnlyHotspot은 데이터사용 불가
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun turnOnHotspot() {
        wifiManager.startLocalOnlyHotspot(object : WifiManager.LocalOnlyHotspotCallback() {
            override fun onStarted(reservation: WifiManager.LocalOnlyHotspotReservation) {
                super.onStarted(reservation)
                val ssid = reservation.wifiConfiguration.SSID
                val pw = reservation.wifiConfiguration.preSharedKey
                Log.e("hotspot", "Wifi Hotspot is on now $ssid")
                Log.e("hotspot", "Wifi Hotspot is on now $pw")
//                mReservation = reservation
            }

            override fun onStopped() {
                super.onStopped()
                Log.e("hotspot", "onStopped: ")
            }

            override fun onFailed(reason: Int) {
                super.onFailed(reason)
                Log.e("hotspot", "onFailed: ")
            }
        }, Handler())
    }


    interface OnTripStartListener {
        fun onTripStart()
    }
}