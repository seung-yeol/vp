package voipot.jnu.conoz.com.voipotguide.ui.schedule

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import kotlinx.android.synthetic.main.fragment_schedule.*
import voipot.jnu.conoz.com.voipotguide.CommonApp
import voipot.jnu.conoz.com.voipotguide.DirectoryActivity
import voipot.jnu.conoz.com.voipotguide.R
import voipot.jnu.conoz.com.voipotguide.databinding.DialogWriteScheduleBinding
import voipot.jnu.conoz.com.voipotguide.databinding.FragmentScheduleBinding
import voipot.jnu.conoz.com.voipotguide.ui.base.BaseFragment
import voipot.jnu.conoz.com.voipotguide.ui.main.SharedPagerViewModel
import voipot.jnu.conoz.com.voipotguide.ui.schedule.adapter.*
import voipot.jnu.conoz.com.voipotguide.util.ExcelUtil
import voipot.jnu.conoz.com.voipotguide.util.IDataManager
import voipot.jnu.conoz.com.voipotguide.util.activityViewModelProvider
import voipot.jnu.conoz.com.voipotguide.util.viewModelProvider
import javax.inject.Inject

const val SCHEDULE_REQUEST_CODE = 11

class ScheduleFragment : BaseFragment<FragmentScheduleBinding>() {
    override val layoutResourceId: Int = R.layout.fragment_schedule

    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var sharedPagerViewModel: SharedPagerViewModel
    private lateinit var scheduleAdapterViewModel: ScheduleAdapterViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val excelUtil: ExcelUtil by lazy {
        ExcelUtil()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                ScheduleFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sharedPagerViewModel = activityViewModelProvider(viewModelFactory)
        scheduleViewModel = viewModelProvider(viewModelFactory)
        scheduleAdapterViewModel = viewModelProvider(viewModelFactory)

        with(viewDataBinding) {
            scheduleViewModel = this@ScheduleFragment.scheduleViewModel
            sharedPagerViewModel = this@ScheduleFragment.sharedPagerViewModel
            scheduleAdapterViewModel = this@ScheduleFragment.scheduleAdapterViewModel
            setLifecycleOwner(this@ScheduleFragment)
        }

        initDialog()
        initRecyclerView()
        observe()
    }

    private lateinit var dialog: DialogPlus
    private fun initDialog() {
        val dwsb = DataBindingUtil.inflate<DialogWriteScheduleBinding>(
                LayoutInflater.from(activity), R.layout.dialog_write_schedule, null, false
        )
        dwsb.scheduleAdapterViewModel = scheduleAdapterViewModel
        dwsb.setLifecycleOwner(this@ScheduleFragment)

        dialog = DialogPlus.newDialog(activity).apply {
            setContentHolder(ViewHolder(dwsb!!.root))

            isExpanded = false
            isCancelable = true
            setGravity(Gravity.BOTTOM)

            contentBackgroundResource = android.R.color.transparent
//                        overlayBackgroundResource = android.R.color.transparent
        }.create()
    }

    private fun initRecyclerView() {
        scheduleRecycler.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        scheduleRecycler.adapter = TextScheduleAdapter(this)
    }

    private fun observe() {
        sharedPagerViewModel.apply {
            isModifyingSchedule.observe(this@ScheduleFragment, Observer {
                val adapter: ScheduleListAdapter<*> = if (it) {
                    sharedPagerViewModel.setExpand(true)
                    ModifyScheduleAdapter(activity!!, scheduleAdapterViewModel)
                } else {
                    TextScheduleAdapter(this@ScheduleFragment)
                }

                scheduleRecycler.adapter = adapter

                if (scheduleAdapterViewModel.scheduleData.value.isNullOrEmpty()) {
                    ArrayList()
                } else {
                    ArrayList(scheduleAdapterViewModel.scheduleData.value)
                }.let { list ->
                    adapter.submitList(list)
                }
            })
        }

        scheduleViewModel.apply {
            addScheduleClick.observe(this@ScheduleFragment, Observer {
                if (it) {
                    dialog.show()
                }
            })
        }

        scheduleAdapterViewModel.apply {
            doneClick.observe(this@ScheduleFragment, Observer {
                if (it) {
                    dialog.dismiss()
                }
            })
        }

        btnOpen.setOnClickListener {
            Intent(activity!!, DirectoryActivity::class.java).let { intent ->
                activity!!.startActivityFromFragment(this@ScheduleFragment, intent, SCHEDULE_REQUEST_CODE)
            }
        }

        /*getCurrentSchedule()?.run {
            txtTime.text = time.toString()
            txtTitle.text = title
            txtMemo.text = memo
        }*/
    }

    /*private fun getCurrentSchedule(): ScheduleEntity? {
        val curTime = CommonApp.getCurrentTime().takeLast(5).split(":").let {
            it[0].toInt() * 60 + it[1].toInt()
        }

        adapter.getAllData().run {
            if (isEmpty()) {
                return null
            }

            forEach { entity ->
                entity.time.toString().split(":").let {
                    if (curTime - it[0].toInt() * 60 + it[1].toInt() < 0) {
                        return entity
                    }
                }
            }
            return last()
        }
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SCHEDULE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val filePath: String?

            if (data!!.hasExtra("fileName")) {
                filePath = data.getStringExtra("fileName")

                excelUtil.fileOpen(filePath!!)
                with((activity!!.application as CommonApp).mDataManager) {
                    overwrite(excelUtil.resultVOList)
                    scheduleRecycler.scheduleChange(getAll())
                }
            }
        }
    }
}

