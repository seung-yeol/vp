package voipot.jnu.conoz.com.voipotguide.adapter

import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import voipot.jnu.conoz.com.voipotguide.R
import voipot.jnu.conoz.com.voipotguide.vo.MessageVO
import java.util.*

class MessageRVAdapter(private var provider: LinkedList<MessageVO>?) : RecyclerView.Adapter<MessageRVAdapter.MyViewHolder>() {
    override fun getItemCount(): Int {
        return provider!!.size
    }

    override fun getItemViewType(position: Int): Int {

        return when (provider!![position].name) {
            "guide" -> 1
            else -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return when (viewType) {
            1 -> {
                RightViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message_right, parent, false))
            }
            2 -> {
                LeftViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message_left, parent, false))
            }
            else -> throw RuntimeException("MessageRecyclerAdapter의 viewType 설정이 안 됐습니다")
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(provider, position)
    }

    fun addAllProvider(provider: List<MessageVO>?) {
        this.provider = LinkedList(provider)
        notifyDataSetChanged()
        Log.e("addAllProvider", "provider.size() : ${provider!!.size}")
    }

    fun addNewMessage(messageVO: MessageVO) {
        provider!!.addFirst(messageVO)
        notifyDataSetChanged()
    }

    fun addOldMessages(messages: List<MessageVO>) {
        provider!!.addAll(messages)
        notifyDataSetChanged()
    }

    abstract class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime = itemView.findViewById<TextView>(R.id.txtTime)!!
        val tvMessage = itemView.findViewById<TextView>(R.id.message)!!

        abstract fun bindData(provider: List<MessageVO>?, position: Int)
    }

    private class LeftViewHolder(itemView: View) : MyViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.txtName)!!

        override fun bindData(provider: List<MessageVO>?, position: Int) {
            tvName.text = provider!![position].name
            tvTime.text = provider[position].time!!.split("-", " ", ":").run {
                "${get(size - 3)}:${get(size - 2)}"
            }
            tvMessage.text = provider[position].message
        }
    }

    private class RightViewHolder(itemView: View) : MyViewHolder(itemView) {
        override fun bindData(provider: List<MessageVO>?, position: Int) {
            tvTime.text = provider!![position].time!!.split("-", " ", ":").run {
                "${get(size - 3)}:${get(size - 2)}"
            }
            tvMessage.text = provider[position].message
        }
    }
}