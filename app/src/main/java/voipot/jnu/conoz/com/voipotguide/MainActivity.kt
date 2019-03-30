//package voipot.jnu.conoz.com.voipotguide

//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.content.pm.PackageManager
//import android.graphics.Color
//import android.location.Location
//import android.location.LocationListener
//import android.location.LocationManager
//import android.os.Build
//import android.os.Bundle
//import android.support.annotation.RequiresApi
//import android.support.v4.content.LocalBroadcastManager
//import androidx.viewpager.widget.ViewPager
//import android.support.v7.app.AppCompatActivity
//import android.transition.TransitionManager
//import android.weakView.View
//import android.weakView.ViewGroup
//import android.weakView.animation.RotateAnimation
//import android.widget.RelativeLayout
//import com.gun0912.tedpermission.PermissionListener
//import com.gun0912.tedpermission.TedPermission
//import com.rd.animation.type.AnimationType
//import kotlinx.android.synthetic.main.activity_main.*
//import kotlinx.android.synthetic.main.fragment_schedule.*
//import org.jetbrains.anko.toast
//import voipot.jnu.conoz.com.voipotguide.adapter.MyPagerAdapter
//import voipot.jnu.conoz.com.voipotguide.fragment.LoginFragment
//import voipot.jnu.conoz.com.voipotguide.fragment.MessageFragment
//import voipot.jnu.conoz.com.voipotguide.fragment.ScheduleFragment
//import voipot.jnu.conoz.com.voipotguide.service.*
//import voipot.jnu.conoz.com.voipotguide.transport.GpsUDP
//import voipot.jnu.conoz.com.voipotguide.transport.MessageUDP
//import voipot.jnu.conoz.com.voipotguide.transport.VoiceUDP
//import voipot.jnu.conoz.com.voipotguide.util.MessageDbHelper
//import voipot.jnu.conoz.com.voipotguide.vo.MessageVO
//import java.util.*
//
//class MainActivity : AppCompatActivity(), LoginFragment.OnTripStartListener {
//    private var isTalk = false
//    private var isExpand = false
//    private var scheduleFragment: ScheduleFragment? = null
//    private var messageFragment: MessageFragment? = null
//    private var messageReceiver: BroadcastReceiver? = null
//    private var connectReceiver: BroadcastReceiver? = null
//    private var apOnReceiver: BroadcastReceiver? = null
//    private val clientDistanceMap: HashMap<String, Location> = hashMapOf()
//    private lateinit var myLocation: Location
//    private lateinit var locationManager: LocationManager
//    private lateinit var myPagerAdapter: MyPagerAdapter
//    private lateinit var messageDbHelper: MessageDbHelper
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        val weakView = window.decorView
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (weakView != null) {
//                weakView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            }
//        } else if (Build.VERSION.SDK_INT >= 21) {
//            window.statusBarColor = Color.BLACK
//        }
//
//        initPermission()
//        initView()
//        initViewPager()
//    }
//
//    private fun initViewPager() {
//        fun writeClick() {
//            expandBottomLayout()
//            btnScheduleBack.visibility = ViewGroup.VISIBLE
//            btnExpand.visibility = ViewGroup.GONE
//            btnAddSchedule.visibility = ViewGroup.VISIBLE
//        }
//
//        pageIndicatorView.apply {
//            setAnimationType(AnimationType.FILL)
//            count = 2
//        }
//
//        MyPagerAdapter(supportFragmentManager).apply {
//            if ((application as CommonApp).isGuide) {
//                messageFragment = MessageFragment.newInstance()
//
//                addFragment(ScheduleFragment.newInstance(object : ScheduleFragment.OnWriteClickListener {
//                    override fun onWriteModeClick() {
//                        writeClick()
//                    }
//                }).also {
//                    scheduleFragment = it
//                })
//                addFragment(messageFragment!!)
//
//                txtLeftPagerTitle.text = resources.getText(R.string.guide)
//                txtRightPagerTitle.text = resources.getText(R.string.schedule)
//
//                runOnUiThread {
//                    TransitionManager.beginDelayedTransition(centerLayout)
//                    btnVoice.visibility = ViewGroup.VISIBLE
//                    durationLayout.visibility = ViewGroup.VISIBLE
//                }
//
//                initBR()
//
//                scanDistance()
//                (application as CommonApp).startIntentService()
//            } else {
//                addFragment(LoginFragment.newInstance())
//                addFragment(ScheduleFragment.newInstance(object : ScheduleFragment.OnWriteClickListener {
//                    override fun onWriteModeClick() {
//                        writeClick()
//                    }
//                }).also {
//                    scheduleFragment = it
//                })
//            }
//
//        }.also {
//            viewPager.adapter = it
//            myPagerAdapter = it
//        }
//
//        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            override fun onPageScrollStateChanged(state: Int) {}
//
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
//
//            override fun onPageSelected(position: Int) {
//                pageIndicatorView.selection = position
//                if ((application as CommonApp).isGuide) {
//                    when (position) {
//                        0 -> {
//                            txtLeftPagerTitle.text = resources.getText(R.string.guide)
//                            txtRightPagerTitle.text = resources.getText(R.string.schedule)
//                        }
//                        1 -> {
//                            txtLeftPagerTitle.text = resources.getText(R.string.message)
//                            txtRightPagerTitle.text = ""
//                        }
//                    }
//                } else {
//                    when (position) {
//                        0 -> {
//                            txtLeftPagerTitle.text = resources.getText(R.string.guide)
//                            txtRightPagerTitle.text = resources.getText(R.string.user)
//                        }
//                        1 -> {
//                            txtLeftPagerTitle.text = resources.getText(R.string.guide)
//                            txtRightPagerTitle.text = resources.getText(R.string.schedule)
//                        }
//                    }
//                }
//            }
//        })
//
//        centerLayout.setOnTouchListener { _, event ->
//            viewPager.onTouchEvent(event)
//        }
//    }
//
//    private fun initBR() {
//        messageReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
//                val recvMessage = intent.getStringExtra(KEY_MESSAGE_RESULT)
//                val recvAddress = intent.getStringExtra(KEY_ADDRESS_RESULT)
//
//                if (recvMessage.isNotEmpty()) {
//                    println("recvMessage : $recvMessage")
//                    messageDbHelper = MessageDbHelper.getInstance(this@MainActivity)
//
//                    MessageVO().apply {
//                        name = CommonApp.getClientName(recvAddress)
//                        message = recvMessage
//                        time = CommonApp.getCurrentTime()
//                    }.let {
//                        messageDbHelper.add(it)
//                        runOnUiThread {
//                            messageFragment?.addMessage(it)
//                        }
//                        CommonApp.clientList.filter{ info ->
//                            info.ip != recvAddress
//                        }.forEach { clientInfo ->
//                            MessageUDP().sendMessage(clientInfo.ip, it)
//                        }
//                    }
//                }
//            }
//        }
//
//        connectReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
//                val recvMessage = intent.getStringExtra(KEY_MESSAGE_RESULT)
//                val recvAddress = intent.getStringExtra(KEY_ADDRESS_RESULT)
//
//                if (recvMessage.isNotEmpty()) {
//                    println("recvMessage : $recvMessage")
//                    messageDbHelper = MessageDbHelper.getInstance(this@MainActivity)
//
//                    MessageVO().apply {
//                        name = CommonApp.getClientName(recvAddress)
//                        message = recvMessage
//                        time = CommonApp.getCurrentTime()
//                    }.let {
//                        messageDbHelper.add(it)
//                        runOnUiThread {
//                            messageFragment?.addMessage(it)
//                        }
//                    }
//                }
//            }
//        }
//
//        apOnReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
//                if (intent.getBooleanExtra(KEY_IS_AP_ON, false)) {
//                    imgConn.visibility = ViewGroup.VISIBLE
//                    imgDisconn.visibility = ViewGroup.GONE
//                } else {
//                    imgConn.visibility = ViewGroup.GONE
//                    imgDisconn.visibility = ViewGroup.VISIBLE
//                }
//            }
//        }
//
//        LocalBroadcastManager.getInstance(this).run {
//            registerReceiver(messageReceiver!!, IntentFilter(ACTION_RECVMESSAGE_RESULT))
//            registerReceiver(connectReceiver!!, IntentFilter(ACTION_RECVCONNECT_RESULT))
//            registerReceiver(apOnReceiver!!, IntentFilter(ACTION_AP_ON_RESULT))
//        }
//    }
//
//    private fun initView() {
//        val voiceSender = VoiceUDP()
//        btnVoice.setOnClickListener {
//            isTalk = if (isTalk) {
//                voiceSender.stopSend()
//                btnVoice.setBackgroundResource(R.drawable.ic_light_sound)
//                false
//            } else {
//                voiceSender.sendVoice()
//                btnVoice.setBackgroundResource(R.drawable.ic_sound)
//                true
//            }
//        }
//
//        btnExpand.setOnClickListener {
//            if (isExpand) {
//                reduceBottomLayout()
//            } else {
//                expandBottomLayout()
//            }
//        }
//
//        btnExit.setOnClickListener {
//            (application as CommonApp).loginOnOff(false)
//
//            messageReceiver?.let { receiver -> LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver) }
//            connectReceiver?.let { receiver -> LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver) }
//
//            runOnUiThread {
//                durationLayout.visibility = ViewGroup.GONE
//
//                messageFragment = MessageFragment.newInstance()
//                runOnUiThread {
//                    TransitionManager.beginDelayedTransition(viewPager)
//                    myPagerAdapter.removeFragment(1)
//                    myPagerAdapter.addFragment(0, LoginFragment.newInstance())
//
//                    TransitionManager.beginDelayedTransition(centerLayout)
//                    btnVoice.visibility = ViewGroup.VISIBLE
//
//                    viewPager.setCurrentItem(0, true)
//                    txtLeftPagerTitle.text = resources.getText(R.string.guide)
//                    txtRightPagerTitle.text = resources.getText(R.string.user)
//                }
//            }
//        }
//
//        btnScheduleBack.setOnClickListener {
//            TransitionManager.beginDelayedTransition(bottomLayout)
//            btnScheduleBack.visibility = ViewGroup.GONE
//            btnExpand.visibility = ViewGroup.VISIBLE
//            scheduleFragment?.cancelRevision()
//        }
//    }
//
//    @SuppressLint("NewApi")
//    private fun initPermission() {
//        fun checkPermission(permissions: String) {
//            if (checkSelfPermission(permissions) != PackageManager.PERMISSION_GRANTED) {
//                TedPermission.with(this)
//                        .setPermissionListener(object : PermissionListener {
//                            override fun onPermissionGranted() {
//                                toast("$permissions Granted")
//                            }
//
//                            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
//                                toast("$permissions Denied")
//                            }
//                        })
//                        .setPermissions(permissions)
//                        .check()
//            }
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            checkPermission(Manifest.permission.RECORD_AUDIO)
//            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//
//            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                TedPermission.with(this)
//                        .setPermissionListener(object : PermissionListener {
//                            override fun onPermissionGranted() {
//                                toast("Permission Granted")
//                                initGPS()
//                            }
//
//                            override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
//                                toast("Permission Denied")
//                            }
//                        })
//                        .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
//                        .check()
//            } else {
//                initGPS()
//            }
//        }
//    }
//
//    private fun expandBottomLayout() {
//        TransitionManager.beginDelayedTransition(bottomLayout)
//        bottomLayout.layoutParams = RelativeLayout.LayoutParams(bottomLayout.layoutParams as RelativeLayout.LayoutParams)
//                .apply {
//                    addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//                    addRule(RelativeLayout.BELOW, R.id.pageIndicatorView)
//                    setMargins(18, 45, 18, 0)
//                }
//
//        if (!isExpand) {
//            btnExpand.startAnimation(RotateAnimation(180.0f, 0.0f, 50.0f, 50.0f).apply {
//                duration = 400
//            })
//            btnExpand.rotation = 180.0f
//        }
//
//        messageFragment?.expandLayout()
//        scheduleFragment?.expandLayout()
//
//        isExpand = true
//    }
//
//    private fun reduceBottomLayout() {
//        TransitionManager.beginDelayedTransition(bottomLayout)
//        RelativeLayout.LayoutParams(bottomLayout.layoutParams as RelativeLayout.LayoutParams)
//                .apply {
//                    addRule(RelativeLayout.BELOW, 0)
//                }
//                .let {
//                    bottomLayout.layoutParams = it
//                }
//
//        if (isExpand) {
//            btnExpand.startAnimation(RotateAnimation(180.0f, 0.0f, 50.0f, 50.0f).apply {
//                duration = 400
//            })
//            btnExpand.rotation = 0.0f
//        }
//
//        messageFragment?.reduceLayout()
//        scheduleFragment?.reduceLayout()
//
//        isExpand = false
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun initGPS() {
//        val currentLocation: Location?
//        myLocation = Location("Guide")
//
//        val locationListener = object : LocationListener {
//            override fun onLocationChanged(location: Location) {
//                myLocation.latitude = location.latitude
//                myLocation.longitude = location.longitude
//            }
//
//            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
//            override fun onProviderEnabled(provider: String) {}
//            override fun onProviderDisabled(provider: String) {}
//        }
//
//        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0.toFloat(), locationListener)
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.toFloat(), locationListener)
//
//        val locationProvider = LocationManager.GPS_PROVIDER
//        currentLocation = locationManager.getLastKnownLocation(locationProvider)
//
//        if (currentLocation != null) {
//            myLocation.latitude = currentLocation.latitude
//            myLocation.longitude = currentLocation.longitude
//        }
//    }
//
//    private fun scanDistance() {
//        val gpsUDP = GpsUDP(object : GpsUDP.ClientDistanceChangeListener {
//            override fun onClientDistanceChange(latLngVO: GpsUDP.LatLngVO) {
//                val clientLocation = Location(latLngVO.name)
//                clientLocation.latitude = latLngVO.latitude
//                clientLocation.longitude = latLngVO.longitude
//
//                clientDistanceMap[latLngVO.name] = clientLocation
//
//                println("????")
//                if (!CommonApp.isConnect(latLngVO.address)) {
//                    println("!!!!!!")
//                }
//
//                if (!CommonApp.isNameFilled) {
//                    CommonApp.clientSetName(latLngVO.name, latLngVO.address)
//                    if (CommonApp.nameFillCheck()) {
//                        println("돌겠다")
//                    }
//                }
//            }
//        })
//        gpsUDP.recvClientLatLng()
//    }
//
//    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
//    override fun onTripStart() {
//        messageFragment = MessageFragment.newInstance()
//        runOnUiThread {
//            TransitionManager.beginDelayedTransition(viewPager)
//            myPagerAdapter.removeFragment(0)
//            myPagerAdapter.addFragment(messageFragment!!)
//
//            TransitionManager.beginDelayedTransition(centerLayout)
//            btnVoice.visibility = ViewGroup.VISIBLE
//            durationLayout.visibility = ViewGroup.VISIBLE
//
//            txtLeftPagerTitle.text = resources.getText(R.string.guide)
//            txtRightPagerTitle.text = resources.getText(R.string.schedule)
//        }
//
//        initBR()
//
//        scanDistance()
//        (application as CommonApp).run {
//            startIntentService()
//            loginOnOff(true)
//        }
//    }
//}
