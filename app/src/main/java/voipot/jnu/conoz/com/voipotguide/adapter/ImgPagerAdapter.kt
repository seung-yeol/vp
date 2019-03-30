package voipot.jnu.conoz.com.voipotguide.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import voipot.jnu.conoz.com.voipotguide.PullImgActivity
import voipot.jnu.conoz.com.voipotguide.R
import java.io.File

class ImgPagerAdapter(imgsList: MutableList<String>?) : PagerAdapter() {
    private val imgsList = ArrayList<String>(imgsList)

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imgItemWidth = container.measuredWidth / 5 - 8
        val layout = LayoutInflater.from(container.context)
                .inflate(R.layout.item_img_pager, container, false) as LinearLayout

        for (i in position * 5..position * 5 + 4) {
            if (i < imgsList.size) {
                ImageView(container.context).apply {
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
                        scaleType = ImageView.ScaleType.FIT_XY
                        setPadding(2, 2, 2, 2)
                    }

                    setOnClickListener {
                        Intent(container.context, PullImgActivity::class.java).apply {
                            putExtra("imgPath", imgsList[i])
                        }.let { intent ->
                            container.context.startActivity(intent)
                        }
                    }

                }.let {
                    Glide.with(container.context).load(File(imgsList[i])).thumbnail(0.05f).into(it)

                    val ll = LinearLayout(container.context).apply {
                        layoutParams = LinearLayout.LayoutParams(imgItemWidth, imgItemWidth).apply {
                            setMargins(4, 2, 4, 2)
                        }
                        setBackgroundResource(R.drawable.edge_rect_indigo)
                        addView(it)
                    }
                    layout.addView(ll)
                }
            } else {
                break
            }
        }

        container.addView(layout)

        return layout
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return if (imgsList.size % 5 == 0) {
            imgsList.size / 5
        } else {
            imgsList.size / 5 + 1
        }
    }
}