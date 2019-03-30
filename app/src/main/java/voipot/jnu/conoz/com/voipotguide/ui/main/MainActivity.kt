package voipot.jnu.conoz.com.voipotguide.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import voipot.jnu.conoz.com.voipotguide.CommonApp
import voipot.jnu.conoz.com.voipotguide.R
import voipot.jnu.conoz.com.voipotguide.adapter.MyPagerAdapter
import voipot.jnu.conoz.com.voipotguide.databinding.ActivityMainBinding
import voipot.jnu.conoz.com.voipotguide.fragment.MessageFragment
import voipot.jnu.conoz.com.voipotguide.transport.GpsUDP
import voipot.jnu.conoz.com.voipotguide.ui.base.BaseActivity
import voipot.jnu.conoz.com.voipotguide.ui.login.LoginFragment
import voipot.jnu.conoz.com.voipotguide.ui.schedule.ScheduleFragment
import voipot.jnu.conoz.com.voipotguide.util.IDataManager
import voipot.jnu.conoz.com.voipotguide.util.viewModelProvider
import java.util.*
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding>(), LoginFragment.OnTripStartListener {
    private var scheduleFragment: ScheduleFragment? = null
    private var messageFragment: MessageFragment? = null
    private val clientDistanceMap: HashMap<String, Location> = hashMapOf()

    private lateinit var myLocation: Location
    private lateinit var locationManager: LocationManager
    private lateinit var myPagerAdapter: MyPagerAdapter

    private lateinit var mainViewModel: MainViewModel
    private lateinit var sharedPagerViewModel: SharedPagerViewModel

    @Inject
    lateinit var mDataManager: IDataManager

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override val layoutResourceId: Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = viewModelProvider(viewModelFactory)
        sharedPagerViewModel = viewModelProvider(viewModelFactory)

        with(viewDataBinding) {
            mainViewModel = this@MainActivity.mainViewModel
            sharedPagerViewModel = this@MainActivity.sharedPagerViewModel
            setLifecycleOwner(this@MainActivity)
        }

        initPermission()
        initViewPager()
        /*if (mDataManager.isLoggedIn) {
            initBR()
        }*/

        makeshift()

        observe()
    }

    private fun observe() {
        mainViewModel.apply {
            tryLogout.observe(this@MainActivity, Observer {
                if (it) {
                    TransitionManager.beginDelayedTransition(viewPager)
                    TransitionManager.beginDelayedTransition(centerLayout)
                    myPagerAdapter.removeFragment(1)
                    myPagerAdapter.addFragment(0, LoginFragment.newInstance())
                }
            })
        }

        sharedPagerViewModel.apply {
            isModifyingSchedule.observe(this@MainActivity, Observer {
                if (!it) {
//                    TransitionManager.beginDelayedTransition(bottomLayout)
                }
            })

            isExpand.observe(this@MainActivity, Observer {
                fun animation(rotation: Float){
                    btnExpand.startAnimation(RotateAnimation(180.0f, 0.0f, 50.0f, 50.0f).apply {
                        duration = 400
                    })
                    btnExpand.rotation = rotation
                }

                TransitionManager.beginDelayedTransition(bottomLayout)
                if (it) {
                    bottomLayout.layoutParams = RelativeLayout.LayoutParams(bottomLayout.layoutParams as RelativeLayout.LayoutParams)
                            .apply {
                                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                                addRule(RelativeLayout.BELOW, R.id.pageIndicatorView)
                                setMargins(18, 45, 18, 0)
                            }
                    animation(180.0f)
                } else {
                    RelativeLayout.LayoutParams(bottomLayout.layoutParams as RelativeLayout.LayoutParams)
                            .apply {
                                addRule(RelativeLayout.BELOW, 0)
                            }
                            .let { lp ->
                                bottomLayout.layoutParams = lp
                            }
                    animation(0.0f)
                }
            })
        }
    }

    //임시방편
    private fun makeshift() {
        txtDuration.text = CommonApp.getCurrentTime().split(" ")[0]
    }

    private fun initViewPager() {
        scheduleFragment = ScheduleFragment.newInstance()
        messageFragment = MessageFragment.newInstance()

        MyPagerAdapter(supportFragmentManager).apply {
            if (mDataManager.isLoggedIn.value!!) {

                addFragment(scheduleFragment!!)
                addFragment(messageFragment!!)

                scanDistance()

                (application as CommonApp).startIntentService()
            } else {
                addFragment(LoginFragment.newInstance())
                addFragment(scheduleFragment!!)
            }
        }.also {
            viewPager.adapter = it
            myPagerAdapter = it
        }

        centerLayout.setOnTouchListener { _, event ->
            viewPager.onTouchEvent(event)
        }
    }

    /*private fun initBR() {
        messageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val clientName = intent.getStringExtra(KEY_CLIENT_NAME)
                val recvMessage = intent.getStringExtra(KEY_MESSAGE_RESULT)
                val recvAddress = intent.getStringExtra(KEY_ADDRESS_RESULT)

                if (recvMessage.isNotEmpty()) {
                    println("recvMessage : $recvMessage")
                    messageDbHelper = MessageDbHelper.getInstance(this@MainActivity)

                    MessageVO().apply {
                        name = clientName
                        message = recvMessage
                        time = CommonApp.getCurrentTime()
                    }.let {
                        messageDbHelper.add(it)
                        runOnUiThread {
                            messageFragment?.addMessage(it)
                        }
                        CommonApp.clientList.filter { info ->
                            info.ip != recvAddress
                        }.forEach { clientInfo ->
                            MessageUDP().sendMessage(clientInfo.ip, it)
                        }
                    }
                }
            }
        }

        connectReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val recvMessage = intent.getStringExtra(KEY_MESSAGE_RESULT)
                val recvAddress = intent.getStringExtra(KEY_ADDRESS_RESULT)

                if (recvMessage.isNotEmpty()) {
                    println("recvMessage : $recvMessage")
                    messageDbHelper = MessageDbHelper.getInstance(this@MainActivity)

                    MessageVO().apply {
                        name = CommonApp.getClientName(recvAddress)
                        message = recvMessage
                        time = CommonApp.getCurrentTime()
                    }.let {
                        messageDbHelper.add(it)
                        runOnUiThread {
                            messageFragment?.addMessage(it)
                        }
                    }
                }
            }
        }

        apOnReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.getBooleanExtra(KEY_IS_AP_ON, false)) {
                    imgConn.visibility = ViewGroup.VISIBLE
                    imgDisconn.visibility = ViewGroup.GONE
                } else {
                    imgConn.visibility = ViewGroup.GONE
                    imgDisconn.visibility = ViewGroup.VISIBLE
                }
            }
        }

        LocalBroadcastManager.getInstance(this).run {
            registerReceiver(messageReceiver!!, IntentFilter(ACTION_RECVMESSAGE_RESULT))
            registerReceiver(connectReceiver!!, IntentFilter(ACTION_RECVCONNECT_RESULT))
            registerReceiver(apOnReceiver!!, IntentFilter(ACTION_AP_ON_RESULT))
        }
    }*/

    @SuppressLint("NewApi")
    private fun initPermission() {
        fun checkPermission(permissions: String) {
            if (checkSelfPermission(permissions) != PackageManager.PERMISSION_GRANTED) {
                TedPermission.with(this)
                        .setPermissionListener(object : PermissionListener {
                            override fun onPermissionGranted() {
                                toast("$permissions Granted")
                            }

                            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                                toast("$permissions Denied")
                            }
                        })
                        .setPermissions(permissions)
                        .check()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission(Manifest.permission.RECORD_AUDIO)
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                TedPermission.with(this)
                        .setPermissionListener(object : PermissionListener {
                            override fun onPermissionGranted() {
                                toast("Permission Granted")
                                initGPS()
                            }

                            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
                                toast("Permission Denied")
                            }
                        })
                        .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        .check()
            } else {
                initGPS()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initGPS() {
        val currentLocation: Location?
        myLocation = Location("Guide")

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                myLocation.latitude = location.latitude
                myLocation.longitude = location.longitude
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0.toFloat(), locationListener)
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.toFloat(), locationListener)

        val locationProvider = LocationManager.GPS_PROVIDER
        currentLocation = locationManager.getLastKnownLocation(locationProvider)

        if (currentLocation != null) {
            myLocation.latitude = currentLocation.latitude
            myLocation.longitude = currentLocation.longitude
        }
    }

    private fun scanDistance() {
        val gpsUDP = GpsUDP(object : GpsUDP.ClientDistanceChangeListener {
            override fun onClientDistanceChange(latLngVO: GpsUDP.LatLngVO) {
                val clientLocation = Location(latLngVO.name)
                clientLocation.latitude = latLngVO.latitude
                clientLocation.longitude = latLngVO.longitude

                clientDistanceMap[latLngVO.name] = clientLocation

                println("????")
                if (!CommonApp.isConnect(latLngVO.address)) {
                    println("!!!!!!")
                }

                if (!CommonApp.isNameFilled) {
                    CommonApp.clientSetName(latLngVO.name, latLngVO.address)
                    if (CommonApp.nameFillCheck()) {
                        println("돌겠다")
                    }
                }
            }
        })
        gpsUDP.recvClientLatLng()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onTripStart() {
        messageFragment = MessageFragment.newInstance()
        runOnUiThread {
            TransitionManager.beginDelayedTransition(viewPager)
            myPagerAdapter.removeFragment(0)
            myPagerAdapter.addFragment(messageFragment!!)

            TransitionManager.beginDelayedTransition(centerLayout)
            btnVoice.visibility = ViewGroup.VISIBLE
            durationLayout.visibility = ViewGroup.VISIBLE

            txtLeftPagerTitle.text = resources.getText(R.string.guide)
            txtRightPagerTitle.text = resources.getText(R.string.schedule)
        }

//        initBR()

        scanDistance()
        (application as CommonApp).run {
            startIntentService()
            loginOnOff(true)
        }
    }
}