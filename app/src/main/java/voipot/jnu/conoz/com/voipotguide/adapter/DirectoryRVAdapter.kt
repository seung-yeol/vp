package voipot.jnu.conoz.com.voipotguide.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_directory.view.*
import voipot.jnu.conoz.com.voipotguide.R
import voipot.jnu.conoz.com.voipotguide.vo.FileVO

class DirectoryRVAdapter(files: List<FileVO>, private var itemClickListener: OnDirectoryClickListener) : RecyclerView.Adapter<DirectoryRVAdapter.DirectoryViewHolder>() {
    private var files = ArrayList(files)
    lateinit var context: Context

    override fun getItemCount(): Int {
        return files.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryViewHolder {
        context = parent.context
        return DirectoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_directory, parent, false))
    }

    override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) {
        holder.apply {
            files[position].let { fileVO ->

                fileName.text = fileVO.name

                if (fileVO.isFile) {
                    fileImg.visibility = ViewGroup.VISIBLE
                    folderImg.visibility = ViewGroup.GONE
                    childCount.visibility = View.GONE
                } else {
                    fileImg.visibility = ViewGroup.GONE
                    folderImg.visibility = ViewGroup.VISIBLE
                    childCount.text = "${fileVO.count.toString()}개"
                    childCount.visibility = View.VISIBLE
                }

                itemView.setOnClickListener {
                    itemClickListener.onDirectoryClick(fileVO)
                }

                if (fileVO.isBack) childCount.visibility = View.GONE
            }
        }
    }

    fun refreshFiles(files: List<FileVO>) {
        this.files = ArrayList(files)
        notifyDataSetChanged()
    }

    interface OnDirectoryClickListener {
        fun onDirectoryClick(fileVO: FileVO)
    }

    class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var fileImg = itemView.fileImg!!
        var folderImg = itemView.folderImg!!
        var fileName = itemView.fileName!!
        var childCount = itemView.count!!
        var divider = itemView.divider!!
    }
}