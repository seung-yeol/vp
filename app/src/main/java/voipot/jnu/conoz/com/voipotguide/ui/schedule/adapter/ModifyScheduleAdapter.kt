package voipot.jnu.conoz.com.voipotguide.ui.schedule.adapter

import android.app.Activity
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.transition.Slide
import android.transition.TransitionManager
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_add_img.view.*
import kotlinx.android.synthetic.main.item_modifiable_schedule.view.*
import kotlinx.android.synthetic.main.view_schedule_delete.view.*
import voipot.jnu.conoz.com.voipotguide.CommonApp
import voipot.jnu.conoz.com.voipotguide.R
import voipot.jnu.conoz.com.voipotguide.adapter.AddImgRVAdapter
import voipot.jnu.conoz.com.voipotguide.adapter.ImgPagerAdapter
import voipot.jnu.conoz.com.voipotguide.databinding.ItemModifiableScheduleBinding
import voipot.jnu.conoz.com.voipotguide.room.Time
import voipot.jnu.conoz.com.voipotguide.transport.ScheduleSendTCP
import voipot.jnu.conoz.com.voipotguide.ui.base.BaseViewHolder
import voipot.jnu.conoz.com.voipotguide.util.IScheduleManager

class ModifyScheduleAdapter(private val context: Context,
                            private val scheduleAdapterViewModel: ScheduleAdapterViewModel) :
        ScheduleListAdapter<ModifyScheduleAdapter.ModifiableHolder>() {
    private val scheduleManager: IScheduleManager = ((context as Activity).application as CommonApp).mDataManager

    private val scheduleSendTCP: ScheduleSendTCP by lazy {
        ScheduleSendTCP()
    }
    private val imm by lazy {
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModifiableHolder {
        return ModifiableHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_modifiable_schedule, parent, false))
    }

    override fun onBindViewHolder(holder: ModifiableHolder, position: Int) {
        val item = getItem(position)

        holder.apply {
            time.text = item.time.toString()
            txtTitle.text = item.title
            txtMemo.text = item.memo

            editTitle.setText(item.title)
            editMemo.setText(item.memo)

            if (item.imgList.isEmpty()) {
                imgsLayout.visibility = ViewGroup.GONE
            } else {
                imgMaxPage = (item.imgList.size - 1) / 5

                if (imgMaxPage == 0) {
                    btnNextImg.visibility = ViewGroup.GONE
                }

                imgsLayout.visibility = ViewGroup.VISIBLE
                imgsPager.adapter = ImgPagerAdapter(item.imgList)
            }
        }

        initListener(holder, position)
    }

    private fun initListener(holder: ModifiableHolder, position: Int) {
        val item = getItem(position)
        holder.apply {
            txtDone.setOnClickListener {
                TransitionManager.beginDelayedTransition(itemView.parentLayout)
                writeLayout.visibility = ViewGroup.GONE
                modifiableLayout.visibility = ViewGroup.VISIBLE

                item.time = Time(0, 0)
                item.title = editTitle.text.toString()
                item.memo = editMemo.text.toString()
                notifyItemChanged(position)

                imm.hideSoftInputFromWindow(editMemo.windowToken, 0)

                Observable.just(scheduleManager)
                        .subscribeOn(Schedulers.io())
                        .subscribe {
                            it.update(item)
                        }

                CommonApp.clientList.forEach { info ->
                    scheduleSendTCP.sendSchedule(info.ip, item)
                }
            }
            btnTrashCan.setOnClickListener {
                fun slideRight(holder: ModifiableHolder) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        TransitionManager.beginDelayedTransition(holder.itemView.parentLayout, Slide(Gravity.END))
                    } else {
                        TransitionManager.beginDelayedTransition(holder.itemView.parentLayout)
                    }
                }

                LayoutInflater.from(context)
                        .inflate(R.layout.view_schedule_delete, holder.itemView.parentLayout, false)
                        .apply {
                            layoutParams.height = holder.itemView.parentLayout.height

                            //뒤에 중첩되 있는 뷰(펜, 휴지통)의 클릭을 막기위해
                            setOnTouchListener { _: View, _: MotionEvent ->
                                true
                            }

                            btnYes.setOnClickListener {
                                Observable.just(scheduleManager)
                                        .subscribeOn(Schedulers.io())
                                        .subscribe { scheduleManager.delete(item.id!!) }
                                        .isDisposed

                                TransitionManager.beginDelayedTransition(itemView.parentLayout)
                                holder.itemView.parentLayout.removeView(this)

                                scheduleAdapterViewModel.removeSchedule(item)
                            }
                            btnNo.setOnClickListener {
                                slideRight(holder)
                                holder.itemView.parentLayout.removeView(this)
                            }
                        }
                        .run {
                            slideRight(holder)
                            holder.itemView.parentLayout.addView(this)
                        }
            }
            btnAddImg.setOnClickListener {
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                val imageCursor = context.contentResolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // 이미지 컨텐트 테이블
                        projection, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc")// DATA를 출력

                val result = ArrayList<String>(imageCursor!!.count)
                val dataColumnIndex = imageCursor.getColumnIndex(projection[0])

                while (imageCursor.moveToNext()) {
                    val filePath = imageCursor.getString(dataColumnIndex)
                    result.add(filePath)
                }
                imageCursor.close()

                DialogPlus.newDialog(context).apply {
                    LayoutInflater.from(context)
                            .inflate(R.layout.dialog_add_img, LinearLayout(context), false)
                            .apply {
                                rvImgs.layoutManager = LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)
                                rvImgs.adapter = AddImgRVAdapter(result)
                            }.let { view ->
                                setContentHolder(ViewHolder(view))
                            }

                    setOnClickListener { dialog, view ->
                        when (view.id) {
                            R.id.btnDown -> dialog.dismiss()
                            R.id.btnImgExpand -> {
                                TransitionManager.beginDelayedTransition(dialog.findViewById(R.id.addParentLayout) as ViewGroup)
                                dialog.findViewById(R.id.addParentLayout).layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

                                (dialog.findViewById(R.id.rvImgs) as RecyclerView).apply {
                                    layoutManager = GridLayoutManager(context, 3)
                                    (rvImgs.adapter as AddImgRVAdapter).itemWidth = rvImgs.width / 3
                                    rvImgs.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                                }

                                dialog.findViewById(R.id.btnImgExpand).visibility = ViewGroup.GONE
                                dialog.findViewById(R.id.txtCenterSee).visibility = ViewGroup.VISIBLE
                            }
                            R.id.btnUpload -> {
                                ((dialog.findViewById(R.id.rvImgs) as RecyclerView).adapter as AddImgRVAdapter).getSelectImgs()
                                        .let { list ->
                                            if (!list.isEmpty()) {
                                                item.imgList.addAll(list)

                                                imgMaxPage = if (list.size % 5 == 0) {
                                                    list.size / 5
                                                } else {
                                                    list.size / 5 + 1
                                                }

                                                notifyItemChanged(position)

                                                Observable.just(scheduleManager)
                                                        .subscribeOn(Schedulers.io())
                                                        .subscribe {
                                                            it.updateImgs(item)
                                                        }
                                            }
                                        }
                                /*println(CommonApp.clientList.size)

                                CommonApp.clientList.forEach { info ->
                                    scheduleSendTCP.sendSchedule(info.ip, item)
                                }*/

                                dialog.dismiss()
                            }
                        }
                    }

                    isExpanded = false
                    isCancelable = true
                    setGravity(Gravity.BOTTOM)

                    contentBackgroundResource = android.R.color.transparent
                }.create().show()
            }

            imgsPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    imgCurrentPage = position

                    if (imgCurrentPage >= imgMaxPage) {
                        btnNextImg.visibility = ViewGroup.GONE
                    }

                    if (position <= 0) {
                        btnPreImg.visibility = ViewGroup.GONE

                        if (btnNextImg.visibility == ViewGroup.GONE) {
                            btnNextImg.visibility = ViewGroup.VISIBLE
                        }
                    } else if (position >= imgMaxPage) {
                        btnNextImg.visibility = ViewGroup.GONE

                        if (btnPreImg.visibility == ViewGroup.GONE) {
                            btnPreImg.visibility = ViewGroup.VISIBLE
                        }
                    } else if (position == 1 || position == imgMaxPage - 1) {
                        btnNextImg.visibility = ViewGroup.VISIBLE
                        btnPreImg.visibility = ViewGroup.VISIBLE
                    }
                }
            })

            btnNextImg.setOnClickListener {
                imgsPager.currentItem = imgCurrentPage + 1
            }
            btnPreImg.setOnClickListener {
                imgsPager.currentItem = imgCurrentPage - 1
            }
        }
    }


    class ModifiableHolder(itemView: View) : BaseViewHolder<ItemModifiableScheduleBinding>(itemView) {
        val modifiableLayout = itemView.modifiableLayout!!
        val time = itemView.txtTime!!
        val txtTitle = itemView.txtTitle!!
        val txtMemo = itemView.txtMemo!!
        val btnTrashCan = itemView.btnTrashCan!!
        val btnAddImg = itemView.btnAddImg!!
        val btnWrite = itemView.btnModify!!.apply {
            setOnClickListener {
                TransitionManager.beginDelayedTransition(itemView.parentLayout)
                itemView.modifiableLayout.visibility = ViewGroup.GONE
                itemView.writeLayout.visibility = ViewGroup.VISIBLE
            }
        }

        val writeLayout = itemView.writeLayout!!
        val editTitle = itemView.editTitle!!
        val editMemo = itemView.editMemo!!
        val txtDone = itemView.txtDone!!
        val imgsLayout = itemView.imgsLayout!!
        val imgsPager = itemView.imgsPager!!
        val btnPreImg = itemView.btnPreImg!!
        val btnNextImg = itemView.btnNextImg!!
        var imgCurrentPage = 0
        var imgMaxPage = 0
    }
}
