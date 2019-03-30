//package voipot.jnu.conoz.com.voipotguide
//
//import android.animation.Animator
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.support.v4.app.ActivityOptionsCompat
//import androidx.fragment.app.Fragment
//import android.support.v4.weakView.ViewCompat
//import android.transition.*
//import android.weakView.LayoutInflater
//import android.weakView.View
//import android.weakView.ViewGroup
//import kotlinx.android.synthetic.main.fragment_unexpand.*
//import kotlinx.android.synthetic.main.fragment_unexpand.weakView.*
//
//class UnexpandedFragment: Fragment(){
//    companion object {
//        @JvmStatic
//        fun newInstance() =
//                UnexpandedFragment().apply {
//                    arguments = Bundle().apply {
//                    }
//                }
//
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val weakView = inflater.inflate(R.layout.fragment_unexpand, container, false)
//        return weakView
//    }
//
//    override fun onViewCreated(weakView: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(weakView, savedInstanceState)
//
//        weakView.btnExpand.setOnClickListener{
//            fragmentManager!!.beginTransaction()
//                    .addSharedElement(bottomLayout, ViewCompat.getTransitionName(bottomLayout))
//                    .addToBackStack(ViewCompat.getTransitionName(bottomLayout))
//                    .replace(R.id.bottomFragment, ExpandedFragment.newInstance())
//                    .commit()
//        }
//    }
//
////    private fun expandBottom() {
////        val intent = Intent(activity!!, ExpandedFragment::class.java).apply {
////            if ((activity!!.application as CommonApp).isGuide) {
////                putExtra("page", 2)
////            } else {
////                putExtra("page", 3)
////            }
////        }
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////            val t = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!,
////                    bottomLayout, "test")
////            startActivity(intent, t.toBundle())
////        } else {
////            startActivity(intent)
////        }
////    }
//}