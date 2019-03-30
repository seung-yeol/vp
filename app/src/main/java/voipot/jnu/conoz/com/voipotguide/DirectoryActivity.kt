package voipot.jnu.conoz.com.voipotguide

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_drectory.*
import kotlinx.android.synthetic.main.item_path.*
import voipot.jnu.conoz.com.voipotguide.adapter.DirectoryRVAdapter
import voipot.jnu.conoz.com.voipotguide.adapter.DirectoryRVAdapter.OnDirectoryClickListener
import voipot.jnu.conoz.com.voipotguide.util.SDCard
import voipot.jnu.conoz.com.voipotguide.vo.FileVO
import java.io.File
import java.util.*

class DirectoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drectory)

        val view = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                view.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        } else if (Build.VERSION.SDK_INT >= 21) {
            window.statusBarColor = Color.BLACK
        }

        initView()
        initDirectory()
    }

    var stackViews = Stack<View>()

    private fun initView(){
        btnDirectoryFinish.setOnClickListener {
            finish()
        }
    }

    private fun initDirectory() {
        val innerPath = File(Environment.getExternalStorageDirectory().path)
        val sdcardPath = File(SDCard.externalSDCardPath)

        val fileVOList = LinkedList<FileVO>()
        fileVOList.add(makeFileVO(innerPath, true).apply { name = "내부저장소" })
        fileVOList.add(makeFileVO(sdcardPath, true).apply { name = "SDCard" })

        directoryRecycler.adapter = DirectoryRVAdapter(fileVOList, object : OnDirectoryClickListener {
            override fun onDirectoryClick(fileVO: FileVO) {
                when {
                    fileVO.isFile -> {
                        Intent().putExtra("fileName", fileVO.parentPath + "/" + fileVO.name).let {
                            setResult(Activity.RESULT_OK, it)
                            finish()
                        }
                    }
                    fileVO.isBack -> {
                        if (stackViews.size == 1) {
                            pathScroll.removeView(stackViews.pop())

                            val fileVOs = LinkedList<FileVO>()
                            fileVOs.add(makeFileVO(innerPath, true).apply { name = "내부저장소" })
                            fileVOs.add(makeFileVO(sdcardPath, true).apply { name = "SDCard" })

                            (directoryRecycler.adapter as DirectoryRVAdapter).refreshFiles(fileVOs)
                        } else {
                            pathScroll.removeView(stackViews.pop())
                            (directoryRecycler.adapter as DirectoryRVAdapter).refreshFiles(getFiles(fileVO.nextPath!!))
                        }
                    }
                    else -> {
                        layoutInflater.inflate(R.layout.item_path, null).apply {
                            pathName.text = fileVO.name
                        }.also {
                            pathScroll.addView(it)
                            stackViews.push(it)
                        }

                        (directoryRecycler.adapter as DirectoryRVAdapter).refreshFiles(getFiles(fileVO.nextPath!!))
                    }
                }
            }
        })
        directoryRecycler.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun makeFileVO(path: File, isRoot: Boolean): FileVO {
        return FileVO().apply {
            name = path.name
            isFile = path.isFile

            parentPath =
                    if (isRoot) null
                    else path.parent

            this.nextPath = path.absolutePath
            if (!isFile) {
                childFiles = path.listFiles()
                count = path.listFiles().size
            }
        }
    }

    private fun getFiles(nextPath: String): LinkedList<FileVO> {
        val rootPath = File(nextPath)
        val files = rootPath.listFiles()
        val fileVOList = LinkedList<FileVO>()

        FileVO().apply {
            name = "..."
            parentPath = rootPath.parent
            isBack = true
            isFile = false
            count = 0
            this.nextPath = rootPath.parent
        }.also {
            fileVOList.add(it)
        }

        for (file in files) {
            if (file.listFiles() == null && !file.isFile) continue
            else if (file.isFile) {
                val fileExtension = file.name.substring(file.name.lastIndexOf(".") + 1, file.name.length)
                if (fileExtension != "xls" && fileExtension != "xlsx") {
                    continue
                }
            }
            makeFileVO(file, false).also {
                fileVOList.add(it)
            }
        }

        return fileVOList
    }
}