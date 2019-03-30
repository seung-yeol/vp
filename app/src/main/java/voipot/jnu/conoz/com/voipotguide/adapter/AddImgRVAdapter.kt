package voipot.jnu.conoz.com.voipotguide.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_add_imgs.view.*
import voipot.jnu.conoz.com.voipotguide.R
import java.io.File
import java.util.*


class AddImgRVAdapter(private val imgPaths: ArrayList<String>) : RecyclerView.Adapter<AddImgRVAdapter.Holder>() {
    private lateinit var context: Context
    private val sequenceList = ArrayList<Int>()
    var itemWidth: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        context = parent.context

        return Holder(LayoutInflater.from(context).inflate(R.layout.item_add_imgs, parent, false))
    }

    override fun getItemCount(): Int {
        return imgPaths.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        println("position = $position")

        //GridLayoutManager로 바뀔때 가로세로 크기 정사각형으로 맞춰줄려고
        itemWidth?.let {
            holder.itemView.layoutParams.width = it
            holder.itemView.layoutParams.height = it
        }

        if (sequenceList.contains(position)) {
            println("visible position = $position")
            holder.selectEdge.visibility = ViewGroup.VISIBLE
            holder.txtSequence.text = (sequenceList.indexOf(position) + 1).toString()
        } else {
            holder.selectEdge.visibility = ViewGroup.GONE
        }

        holder.itemView.setOnClickListener {
            if (holder.selectEdge.visibility == ViewGroup.GONE) {
                holder.selectEdge.visibility = ViewGroup.VISIBLE

                holder.txtSequence.text = (sequenceList.size + 1).toString()

                sequenceList.add(position)
            } else {
                holder.selectEdge.visibility = ViewGroup.GONE

                val target = sequenceList.indexOf(position)
                sequenceList.remove(position)
                sequenceList.mapIndexed { index, value ->
                    if (index >= target) {
                        notifyItemChanged(value)
                    }
                }
            }
        }

        val file = File(imgPaths[position])
        Glide.with(context).load(file).thumbnail(0.1f).into(holder.img)
    }

    fun getSelectImgs(): LinkedList<String> {
        return LinkedList<String>().apply {
            sequenceList.forEach {
                add(imgPaths[it])
            }
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img = itemView.imgFile!!
        val selectEdge = itemView.imgEdge!!
        val txtSequence = itemView.txtSequence!!
    }
}