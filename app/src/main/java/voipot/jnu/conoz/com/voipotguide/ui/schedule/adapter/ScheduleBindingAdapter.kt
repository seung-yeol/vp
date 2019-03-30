package voipot.jnu.conoz.com.voipotguide.ui.schedule.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import voipot.jnu.conoz.com.voipotguide.room.entity.ScheduleEntity

@BindingAdapter("scheduleChange")
fun RecyclerView.scheduleChange(scheduleData: List<ScheduleEntity>?) {

    scheduleData?.let {
        scheduleData.forEach {
            println("img = ${it.joinedImgString} imgCount = ${it.imgCount}")
        }

        (adapter as ScheduleListAdapter).apply {
            submitList(ArrayList(it)) {
                scrollToPosition(itemCount - 1)
            }
        }
    }
}