package voipot.jnu.conoz.com.voipotguide.ui

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.viewpager.widget.ViewPager

@BindingAdapter("currentPage")
fun ViewPager.setCurrentPage(currentPage: Int) {
    setCurrentItem(currentPage, true)
}

@InverseBindingAdapter(attribute = "currentPage", event = "onPageSelect")
fun ViewPager.getCurrentPage(): Int{
    return currentItem
}

@BindingAdapter("onPageSelect")
fun ViewPager.onPageChange(pageChange: InverseBindingListener){
    addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            pageChange.onChange()
        }
    })
}

@BindingAdapter("imgRes")
fun setImgRes(imgBtn: ImageButton, resId: Int) {
    imgBtn.setBackgroundResource(resId)
}

@BindingAdapter("visibleOrGone")
fun setVisibleOrGone(viewGroup: ViewGroup, visible: Boolean) {
    viewGroup.visibility = if (visible) ViewGroup.VISIBLE else ViewGroup.GONE
}

@BindingAdapter("visibleOrGone")
fun setVisibleOrGone(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}
