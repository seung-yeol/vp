package voipot.jnu.conoz.com.voipotguide

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_pull_img.*
import java.io.File

class PullImgActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pull_img)

        val imgPath = intent.getStringExtra("imgPath")
        Glide.with(this).load(File(imgPath)).into(imgPull)
    }
}