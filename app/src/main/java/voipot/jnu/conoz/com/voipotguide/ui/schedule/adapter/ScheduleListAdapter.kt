package voipot.jnu.conoz.com.voipotguide.ui.schedule.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import voipot.jnu.conoz.com.voipotguide.room.entity.ScheduleEntity

abstract class ScheduleListAdapter<VH : RecyclerView.ViewHolder> :
        ListAdapter<ScheduleEntity, VH>(
                object : DiffUtil.ItemCallback<ScheduleEntity>() {
                    override fun areItemsTheSame(oldItem: ScheduleEntity, newItem: ScheduleEntity): Boolean {
                        return oldItem.id == newItem.id
                    }

                    override fun areContentsTheSame(oldItem: ScheduleEntity, newItem: ScheduleEntity): Boolean {
                        return (oldItem.memo == newItem.memo) && (oldItem.title == oldItem.title) && (oldItem.time == oldItem.time)
                    }
                }
        ) {
    protected var clickable = true
}