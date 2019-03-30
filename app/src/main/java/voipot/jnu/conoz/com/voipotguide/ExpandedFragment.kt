//package voipot.jnu.conoz.com.voipotguide
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.os.Build
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentTransaction
//import android.support.v4.content.LocalBroadcastManager
//import android.support.v4.weakView.ViewCompat
//import androidx.viewpager.widget.ViewPager
//import android.transition.ChangeTransform
//import android.transition.TransitionInflater
//import android.weakView.LayoutInflater
//import android.weakView.View
//import android.weakView.ViewGroup
//import com.rd.animation.type.AnimationType
//import kotlinx.android.synthetic.main.activity_main.*
//import kotlinx.android.synthetic.main.fragment_expand.*
//import kotlinx.android.synthetic.main.fragment_expand.weakView.*
//import kotlinx.android.synthetic.main.fragment_schedule.*
//import org.jetbrains.anko.support.v4.runOnUiThread
//import voipot.jnu.conoz.com.voipotguide.adapter.MyPagerAdapter
//import voipot.jnu.conoz.com.voipotguide.fragment.ExpandedLoginFragment
//import voipot.jnu.conoz.com.voipotguide.fragment.ExpandedMessageFragment
//import voipot.jnu.conoz.com.voipotguide.fragment.ExpandedScheduleFragment
//import voipot.jnu.conoz.com.voipotguide.service.*
//import voipot.jnu.conoz.com.voipotguide.transport.MessageUDP
//import voipot.jnu.conoz.com.voipotguide.util.MessageDbHelper
//import voipot.jnu.conoz.com.voipotguide.vo.MessageVO
//
//class ExpandedFragment : Fragment() {
//    private var position = 0
//    private var scheduleFragment: ExpandedScheduleFragment? = null
//    private var messageFragment: ExpandedMessageFragment? = null
//    private var myPagerAdapter: MyPagerAdapter? = null
//
//    private var messageReceiver: BroadcastReceiver? = null
//    private var connectReceiver: BroadcastReceiver? = null
//    private var apOnReceiver: BroadcastReceiver? = null
//
//    private val messageDbHelper: MessageDbHelper by lazy {
//        MessageDbHelper.getInstance(activity!!)
//    }
//
//    companion object {
//        @JvmStatic
//        fun newInstance() =
//                ExpandedFragment().apply {
//                    arguments = Bundle().apply {
//                    }
//                }
//
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
//        }
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val weakView = inflater.inflate(R.layout.fragment_expand, container, false)
//        return weakView
//    }
//
//    override fun onViewCreated(weakView: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(weakView, savedInstanceState)
//
//        weakView.btnExpand.setOnClickListener{
//            fragmentManager!!.beginTransaction()
//                    .addSharedElement(bottomLayout, ViewCompat.getTransitionName(bottomLayout))
//                    .addToBackStack("tttt")
//                    .replace(R.id.bottomFragment, UnexpandedFragment.newInstance())
//                    .commit()
//        }
//
////        initViewPager()
//    }
//
//    /*private fun initViewPager() {
//        pageIndicatorView.apply {
//            setAnimationType(AnimationType.FILL)
//            count = 2
//        }
//
//        MyPagerAdapter(activity!!.supportFragmentManager).apply {
//            fun writeClick() {
//                btnScheduleBack.visibility = ViewGroup.VISIBLE
//                btnExpand.visibility = ViewGroup.GONE
//                btnAddSchedule.visibility = ViewGroup.VISIBLE
//            }
//
//            if ((activity!!.application as CommonApp).isGuide) {
//                messageFragment = ExpandedMessageFragment.newInstance()
//
//                addFragment(ExpandedScheduleFragment.newInstance(object : ExpandedScheduleFragment.OnWriteClickListener {
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
////                runOnUiThread {
////                    TransitionManager.beginDelayedTransition(centerLayout)
////                    btnVoice.visibility = ViewGroup.VISIBLE
////                    durationLayout.visibility = ViewGroup.VISIBLE
////                }
//
//                initBR()
//
//                (activity!!.application as CommonApp).startIntentService()
//            } else {
//                addFragment(ExpandedLoginFragment.newInstance())
//                addFragment(ExpandedScheduleFragment.newInstance(object : ExpandedScheduleFragment.OnWriteClickListener {
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
////        position = intent.getIntExtra("position", 0)
//        pageIndicatorView.selection = 0
//
//        if ((activity!!.application as CommonApp).isGuide) {
//            when (position) {
//                0 -> {
//                    txtLeftPagerTitle.text = resources.getText(R.string.guide)
//                    txtRightPagerTitle.text = resources.getText(R.string.schedule)
//                }
//                1 -> {
//                    txtLeftPagerTitle.text = resources.getText(R.string.message)
//                    txtRightPagerTitle.text = ""
//                }
//            }
//        } else {
//            when (position) {
//                0 -> {
//                    txtLeftPagerTitle.text = resources.getText(R.string.guide)
//                    txtRightPagerTitle.text = resources.getText(R.string.user)
//                }
//                1 -> {
//                    txtLeftPagerTitle.text = resources.getText(R.string.guide)
//                    txtRightPagerTitle.text = resources.getText(R.string.schedule)
//                }
//            }
//        }
//
//        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            override fun onPageScrollStateChanged(state: Int) {}
//
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
//
//            override fun onPageSelected(position: Int) {
//                pageIndicatorView.selection = position
//                if ((activity!!.application as CommonApp).isGuide) {
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
//    }*/
//
//    private fun initBR() {
//        messageReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
//                val recvMessage = intent.getStringExtra(KEY_MESSAGE_RESULT)
//                val recvAddress = intent.getStringExtra(KEY_ADDRESS_RESULT)
//
//                if (recvMessage.isNotEmpty()) {
//                    println("recvMessage : $recvMessage")
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
//                        CommonApp.clientList.filter { info ->
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
//        LocalBroadcastManager.getInstance(activity!!).run {
//            registerReceiver(messageReceiver!!, IntentFilter(ACTION_RECVMESSAGE_RESULT))
//            registerReceiver(connectReceiver!!, IntentFilter(ACTION_RECVCONNECT_RESULT))
//            registerReceiver(apOnReceiver!!, IntentFilter(ACTION_AP_ON_RESULT))
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//
//        apOnReceiver?.let { receiver -> LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(receiver) }
//        messageReceiver?.let { receiver -> LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(receiver) }
//        connectReceiver?.let { receiver -> LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(receiver) }
//    }
//}