//package voipot.jnu.conoz.com.voipotguide.fragment
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.support.v7.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import android.weakView.Gravity
//import android.weakView.LayoutInflater
//import android.weakView.View
//import android.weakView.ViewGroup
//import android.widget.EditText
//import android.widget.LinearLayout
//import android.widget.TextView
//import com.orhanobut.dialogplus.DialogPlus
//import com.orhanobut.dialogplus.ViewHolder
//import kotlinx.android.synthetic.main.fragment_schedule.*
//import voipot.jnu.conoz.com.voipotguide.ui.schedule.adapter.ModifyScheduleAdapter
//import voipot.jnu.conoz.com.voipotguide.ui.schedule.adapter.ScheduleRVAdapter
//import voipot.jnu.conoz.com.voipotguide.ui.schedule.adapter.TextScheduleAdapter
//import voipot.jnu.conoz.com.voipotguide.CommonApp
//import voipot.jnu.conoz.com.voipotguide.DirectoryActivity
//import voipot.jnu.conoz.com.voipotguide.R
//import voipot.jnu.conoz.com.voipotguide.transport.ScheduleSendTCP
//import voipot.jnu.conoz.com.voipotguide.util.ExcelUtil
//import voipot.jnu.conoz.com.voipotguide.vo.ScheduleVO
//
//class ExpandedScheduleFragment : Fragment() {
//    private lateinit var adapter: ScheduleRVAdapter<out RecyclerView.ViewHolder>
//    private lateinit var writeClickListener: OnWriteClickListener
//    private val scheduleDbHelper by lazy {
//        ScheduleDbHelper.getInstance(activity!!)
//    }
//
//    private val excelUtil: ExcelUtil by lazy {
//        ExcelUtil()
//    }
//
//    interface OnWriteClickListener {
//        fun onWriteModeClick()
//    }
//
//    companion object {
//        @JvmStatic
//        fun newInstance(listener: OnWriteClickListener) =
//                ExpandedScheduleFragment().apply {
//                    arguments = Bundle().apply {
//                    }
//                    writeClickListener = listener
//                }
//
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//        }
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//
//        return layoutInflater.inflate(R.layout.fragment_schedule, container, false)
//    }
//
//    override fun onViewCreated(weakView: View, savedInstanceState: Bundle?) {
//        initRecyclerView()
//        initView()
//    }
//
//    private fun initRecyclerView() {
//        scheduleRecycler.layoutManager = LinearLayoutManager(activity!!, LinearLayout.VERTICAL, false)
//        TextScheduleAdapter(activity!!, arrayListOf()).apply {
//            addAll(scheduleDbHelper.getAll())
//        }.also {
//            scheduleRecycler.adapter = it
//            adapter = it
//        }
//    }
//
//    private fun initView() {
//        btnWrite.setOnClickListener {
//            writeClickListener.onWriteModeClick()
//            scheduleRecycler.adapter = ModifyScheduleAdapter(activity!!, adapter.getAllData()).also { adapter ->
//                this.adapter = adapter
//            }
//            writeLayout.visibility = ViewGroup.GONE
//        }
//
//        btnOpen.setOnClickListener {
//            Intent(activity!!, DirectoryActivity::class.java).let { intent ->
//                activity!!.startActivityFromFragment(this@ExpandedScheduleFragment, intent, SCHEDULE_REQUEST_CODE)
//            }
//        }
//
//        btnAddSchedule.setOnClickListener {
//            DialogPlus.newDialog(activity).apply {
//                setContentHolder(ViewHolder(R.layout.dialog_write_schedule))
//
//                isExpanded = false
//                isCancelable = true
//                setGravity(Gravity.BOTTOM)
//                setOnClickListener { dialog, weakView ->
//                    when (weakView.id) {
//                        R.id.btnDone -> {
//                            val vo = ScheduleVO().apply {
//                                time = (dialog.findViewById(R.id.txtTime2) as TextView).text.toString()
//                                title = (dialog.findViewById(R.id.editTitle) as EditText).text.toString()
//                                memo = (dialog.findViewById(R.id.editMemo) as EditText).text.toString()
//                            }
//                            with(ScheduleDbHelper.getInstance(context)) {
//                                vo.id = add(vo)
//
//                                android.transition.TransitionManager.beginDelayedTransition(scheduleRecycler)
//                                this@ExpandedScheduleFragment.adapter.add(vo, getPosition(vo))
//                            }
//
//                            dialog.dismiss()
//
//                            CommonApp.clientList.forEach { info ->
//                                ScheduleSendTCP().sendSchedule(info.ip, vo)
//                            }
//                        }
//
//                        R.id.btnDown -> {
//                            dialog.dismiss()
//                        }
//                    }
//
//                }
//
//                contentBackgroundResource = android.R.color.transparent
////                overlayBackgroundResource = android.R.color.transparent
//            }.create().show()
//        }
//
//        getCurrentSchedule()?.run {
//            txtTime.text = time
//            txtTitle.text = title
//            txtMemo.text = memo
//        }
//    }
//
//    private fun getCurrentSchedule(): ScheduleVO? {
//        val curTime = CommonApp.getCurrentTime().takeLast(5).split(":").let {
//            it[0].toInt() * 60 + it[1].toInt()
//        }
//
//        adapter.getAllData().run {
//            if (isEmpty()) {
//                return null
//            }
//
//            forEach { scheduleVO ->
//                scheduleVO.time.split(":").let {
//                    if (curTime - it[0].toInt() * 60 + it[1].toInt() < 0) {
//                        return scheduleVO
//                    }
//                }
//            }
//            return last()
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == SCHEDULE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            val filePath: String?
//
//            if (data!!.hasExtra("fileName")) {
//                filePath = data.getStringExtra("fileName")
//
//                excelUtil.fileOpen(filePath!!)
//                with(scheduleDbHelper) {
//                    overwrite(excelUtil.resultVOList)
//                    adapter.addAll(getAll())
//                }
//            }
//        }
//    }
//
//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//    }
//
//    fun cancelRevision() {
//        scheduleRecycler.adapter = TextScheduleAdapter(activity!!, adapter.getAllData()).also { adapter ->
//            this.adapter = adapter
//        }
//        writeLayout.visibility = ViewGroup.VISIBLE
//        btnAddSchedule.visibility = ViewGroup.GONE
//    }
//}
