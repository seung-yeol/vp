package voipot.jnu.conoz.com.voipotguide.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter

class MyPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private val fragmentList : MutableList<Fragment> by lazy {
        ArrayList<Fragment>()
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size

    }

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
        notifyDataSetChanged()
    }

    fun addFragment(position: Int, fragment: Fragment) {
        fragmentList.add(position, fragment)
        notifyDataSetChanged()
    }

    fun removeFragment(position: Int){
        fragmentList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun notifyDataSetChanged() {
        println("notify")
        super.notifyDataSetChanged()
    }

    override fun getItemPosition(`object`: Any): Int {
        return  PagerAdapter.POSITION_NONE
    }
}