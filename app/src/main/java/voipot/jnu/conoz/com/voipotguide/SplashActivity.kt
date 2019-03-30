package voipot.jnu.conoz.com.voipotguide

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import voipot.jnu.conoz.com.voipotguide.ui.main.MainActivity
import java.lang.ref.WeakReference

class SplashActivity : AppCompatActivity() {
    private val handler = SplashActivityHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_splash)

        handler.sendEmptyMessageDelayed(0, 3000)
    }

    private class SplashActivityHandler(activity: SplashActivity) : Handler() {
        private val mActivity: WeakReference<SplashActivity> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val i = Intent(mActivity.get(), MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            mActivity.get()!!.startActivity(i)
        }
    }
}
