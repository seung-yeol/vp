
//package voipot.jnu.conoz.com.voipotguide.fragment
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.EditText
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.jakewharton.rxbinding3.view.clicks
//import com.orhanobut.dialogplus.DialogPlus
//import com.orhanobut.dialogplus.ViewHolder
//import dagger.android.support.DaggerFragment
//import io.reactivex.Observable
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.schedulers.Schedulers
//import kotlinx.android.synthetic.main.fragment_schedule.*
//import voipot.jnu.conoz.com.voipotguide.CommonApp
//import voipot.jnu.conoz.com.voipotguide.DirectoryActivity
//import voipot.jnu.conoz.com.voipotguide.R
//import voipot.jnu.conoz.com.voipotguide.ui.schedule.adapter.ModifyScheduleAdapter
//import voipot.jnu.conoz.com.voipotguide.ui.schedule.adapter.ScheduleRVAdapter
//import voipot.jnu.conoz.com.voipotguide.ui.schedule.adapter.TextScheduleAdapter
//import voipot.jnu.conoz.com.voipotguide.room.Time
//import voipot.jnu.conoz.com.voipotguide.room.entity.ScheduleEntity
//import voipot.jnu.conoz.com.voipotguide.transport.ScheduleSendTCP
//import voipot.jnu.conoz.com.voipotguide.util.ExcelUtil
//import voipot.jnu.conoz.com.voipotguide.util.IDataManager
//
//const val SCHEDULE_REQUEST_CODE = 11
//
//class ScheduleFragment : DaggerFragment() {
//    private lateinit var adapter: ScheduleRVAdapter<out RecyclerView.ViewHolder>
//    private lateinit var writeClickListener: OnWriteClickListener
//    private val dataManager: IDataManager by lazy {
//        (activity!!.application as CommonApp).mDataManager
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
//                ScheduleFragment().apply {
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
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        initRecyclerView()
//        initView()
//    }
//
//    private fun initRecyclerView() {
//        scheduleRecycler.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
//        TextScheduleAdapter(activity!!, arrayListOf()).apply {
//            Observable.just(dataManager)
//                    .subscribeOn(Schedulers.io())
//                    .subscribe { db ->
//                        addAll(db.getAll())
//                    }
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
//                this@ScheduleFragment.adapter = adapter
//            }
//            writeLayout.visibility = ViewGroup.GONE
//        }
//
//        btnOpen.setOnClickListener {
//            Intent(activity!!, DirectoryActivity::class.java).let { intent ->
//                activity!!.startActivityFromFragment(this@ScheduleFragment, intent, SCHEDULE_REQUEST_CODE)
//            }
//        }
//
//        btnAddSchedule.clicks()
//                .subscribe { event ->
//                    DialogPlus.newDialog(activity).apply {
//                        setContentHolder(ViewHolder(R.layout.dialog_write_schedule))
//
//                        isExpanded = false
//                        isCancelable = true
//                        setGravity(Gravity.BOTTOM)
//                        setOnClickListener { dialog, view ->
//                            when (view.id) {
//                                R.id.btnDone -> {
//                                    val entity = ScheduleEntity().apply {
//                                        //                                time = (dialog.findViewById(R.id.txtTime2) as TextView).text.toString()
//                                        time = Time(0, 0)
//                                        title = (dialog.findViewById(R.id.editTitle) as EditText).text.toString()
//                                        memo = (dialog.findViewById(R.id.editMemo) as EditText).text.toString()
//                                    }
//
//                                    Observable.just(dataManager)
//                                            .subscribeOn(Schedulers.io())
//                                            .map {
//                                                entity.id = it.add(entity)
//                                                dataManager.getPosition(entity)
//                                            }
//                                            .observeOn(AndroidSchedulers.mainThread())
//                                            .subscribe { position ->
//                                                android.transition.TransitionManager.beginDelayedTransition(scheduleRecycler)
//
//                                                this@ScheduleFragment.adapter.add(entity, position)
//                                                dialog.dismiss()
//
//                                                CommonApp.clientList.forEach { info ->
//                                                    ScheduleSendTCP().sendSchedule(info.ip, entity)
//                                                }
//                                            }
//                                }
//
//                                R.id.btnDown -> {
//                                    dialog.dismiss()
//                                }
//                            }
//
//                        }
//
//                        contentBackgroundResource = android.R.color.transparent
////                        overlayBackgroundResource = android.R.color.transparent
//                    }.create().show()
//                }
//
//        /*btnAddSchedule.setOnClickListener {
//            DialogPlus.newDialog(activity).apply {
//                setContentHolder(ViewHolder(R.layout.dialog_write_schedule))
//
//                isExpanded = false
//                isCancelable = true
//                setGravity(Gravity.BOTTOM)
//                setOnClickListener { dialog, weakView ->
//                    when (weakView.id) {
//                        R.id.btnDone -> {
//                            val entity = ScheduleEntity().apply {
//                                //                                time = (dialog.findViewById(R.id.txtTime2) as TextView).text.toString()
//                                time = Time(0, 0)
//                                title = (dialog.findViewById(R.id.editTitle) as EditText).text.toString()
//                                memo = (dialog.findViewById(R.id.editMemo) as EditText).text.toString()
//                            }
//
//                            Observable.just(mDataManager)
//                                    .subscribeOn(Schedulers.io())
//                                    .subscribe {
//                                        entity.id = it.add(entity)
//                                    }
//
//
//                            android.transition.TransitionManager.beginDelayedTransition(scheduleRecycler)
//                            this@ScheduleFragment.adapter.add(entity, mDataManager.getPosition(entity))
//
//
//                            dialog.dismiss()
//
//                            CommonApp.clientList.forEach { info ->
//                                ScheduleSendTCP().sendSchedule(info.ip, entity)
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
//        }*/
//
//        getCurrentSchedule()?.run {
//            txtTime.text = time.toString()
//            txtTitle.text = title
//            txtMemo.text = memo
//        }
//    }
//
//    private fun getCurrentSchedule(): ScheduleEntity? {
//        val curTime = CommonApp.getCurrentTime().takeLast(5).split(":").let {
//            it[0].toInt() * 60 + it[1].toInt()
//        }
//
//        adapter.getAllData().run {
//            if (isEmpty()) {
//                return null
//            }
//
//            forEach { entity ->
//                entity.time.toString().split(":").let {
//                    if (curTime - it[0].toInt() * 60 + it[1].toInt() < 0) {
//                        return entity
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
//                with((activity!!.application as CommonApp).mDataManager) {
//                    overwrite(excelUtil.resultVOList)
//                    adapter.addAll(getAll())
//                }
//            }
//        }
//    }
//
//    fun reduceLayout() {
//        smallLayout.visibility = View.VISIBLE
//        scheduleRecycler.visibility = View.GONE
//    }
//
//    fun expandLayout() {
//        smallLayout.visibility = View.GONE
//        scheduleRecycler.visibility = View.VISIBLE
//    }
//
//    fun cancelRevision() {
//        scheduleRecycler.adapter = TextScheduleAdapter(activity!!, adapter.getAllData()).also { adapter ->
//            this@ScheduleFragment.adapter = adapter
//        }
//        writeLayout.visibility = ViewGroup.VISIBLE
//        btnAddSchedule.visibility = ViewGroup.GONE
//    }
//}
