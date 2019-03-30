package voipot.jnu.conoz.com.voipotguide.ui.schedule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import voipot.jnu.conoz.com.voipotguide.adapter.ImgPagerAdapter
import voipot.jnu.conoz.com.voipotguide.databinding.ItemScheduleBinding
import voipot.jnu.conoz.com.voipotguide.room.entity.ScheduleEntity

class TextScheduleAdapter(private val lifecycleOwner: LifecycleOwner) :
        ScheduleListAdapter<ScheduleHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleHolder {
        val binding = ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ScheduleHolder(binding, lifecycleOwner)
    }

    override fun onBindViewHolder(holder: ScheduleHolder, position: Int) {
        val item = ScheduleItem(getItem(position))

        holder.bind(item)
    }
}

class ScheduleItem(val entity: ScheduleEntity) {
    var pageMaxPosition: Int = 0
    var imgCurrentPage = MutableLiveData<Int>()
    val imgsVisibility = MutableLiveData<Boolean>()
    val nextVisibility = MutableLiveData<Boolean>()
    val preVisibility = MutableLiveData<Boolean>()

    init {
        imgCurrentPage.value = 0
        imgsVisibility.value = false
        preVisibility.value = false
        nextVisibility.value = entity.imgList.size > 5
    }
    
    val onPageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)

            if (position >= pageMaxPosition) {
                nextVisibility.value = false
            }

            if (position <= 0) {
                preVisibility.value = false

                if (!nextVisibility.value!!) {
                    nextVisibility.value = true
                }
            } else if (position >= pageMaxPosition) {
                preVisibility.value = false

                if (!preVisibility.value!!) {
                    preVisibility.value = true
                }
            } else if (position == 1 || position == pageMaxPosition - 1) {
                nextVisibility.value = true
                preVisibility.value = true
            }
        }
    }

    fun onNextClick(clickedView: View){
        imgCurrentPage.value = imgCurrentPage.value!!.plus( 1)
    }

    fun onPreClick(clickedView: View){
        imgCurrentPage.value = imgCurrentPage.value!!.minus( 1)
    }
}

class ScheduleHolder(
        private val binding: ItemScheduleBinding,
        private val lifecycleOwner: LifecycleOwner
) : RecyclerView.ViewHolder(binding.root) {
    val imgsPager = binding.imgsPager
    val btnPreImg = binding.btnPreImg
    val btnNextImg = binding.btnNextImg

    fun bind(item: ScheduleItem) {
        binding.scheduleItem = item
        binding.setLifecycleOwner(lifecycleOwner)
        binding.executePendingBindings()

        if (!item.entity.imgList.isNullOrEmpty()) {
            item.imgsVisibility.value = true
            item.pageMaxPosition = (item.entity.imgList.size - 1) / 5

            if (item.pageMaxPosition == 0) {
                item.nextVisibility.value = false
            }

            imgsPager.adapter = ImgPagerAdapter(item.entity.imgList)
        }
    }
}