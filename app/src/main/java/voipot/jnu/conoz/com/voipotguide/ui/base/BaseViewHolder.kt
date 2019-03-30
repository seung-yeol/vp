package voipot.jnu.conoz.com.voipotguide.ui.base

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.adapters.ViewBindingAdapter
import androidx.recyclerview.widget.RecyclerView

open class BaseViewHolder<T : ViewDataBinding >(view: View) : RecyclerView.ViewHolder(view) {
    protected val binding: T = DataBindingUtil.bind(view)!!

}