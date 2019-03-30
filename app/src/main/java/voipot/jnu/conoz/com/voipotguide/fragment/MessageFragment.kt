package voipot.jnu.conoz.com.voipotguide.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_message.*
import voipot.jnu.conoz.com.voipotguide.CommonApp
import voipot.jnu.conoz.com.voipotguide.R
import voipot.jnu.conoz.com.voipotguide.adapter.MessageRVAdapter
import voipot.jnu.conoz.com.voipotguide.transport.MessageUDP
import voipot.jnu.conoz.com.voipotguide.util.MessageDbHelper
import voipot.jnu.conoz.com.voipotguide.vo.MessageVO
import java.util.*

class MessageFragment : Fragment() {
    private var isExpand = false
    private var messagePage = 1
    private var messageDbHelper: MessageDbHelper? = null

    private val messageRecyclerAdapter by lazy { MessageRVAdapter(LinkedList()) }

    companion object {
        @JvmStatic
        fun newInstance() =
                MessageFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)

        if (isVisibleToUser) {
            println("MessageFragment 보인다")
            messagePage = 1
            if (messageDbHelper != null) {
                messageRecyclerAdapter.addAllProvider(messageDbHelper!!.getMessages(messagePage))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setSmallLayout()
        messageDbHelper = MessageDbHelper(activity!!)
        messageRecyclerAdapter.addAllProvider(messageDbHelper!!.getMessages(messagePage))
        recyclerMessage.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        recyclerMessage.adapter = messageRecyclerAdapter


        recyclerMessage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisiblePosition = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                val count = recyclerView.adapter!!.itemCount

                if (lastVisiblePosition == count) {
                    messagePage++
                    (recyclerView.adapter as MessageRVAdapter).addOldMessages(messageDbHelper!!.getMessages(messagePage))
                }
            }
        })

        if (isExpand) {
//            smallLayout.visibility = ViewGroup.GONE
//            recyclerMessage.visibility = ViewGroup.VISIBLE
        }

        btnSend.setOnClickListener {
            if (editMessage.text.isNotEmpty()) {
                editMessage.text.toString().let { editMessage ->
                    //브로드캐스트로 할 수도 있으니 나중에 한번더 체크
                    val messageUDP = MessageUDP()

                    val vo = MessageVO().apply {
                        name = "guide"
                        message = editMessage
                        time = CommonApp.getCurrentTime()
                        isRead = true
                    }

                    messageDbHelper!!.add(vo)
                    activity!!.runOnUiThread {
                        addMessage(vo)
                    }

                    for (clientInfo in CommonApp.clientList) {
                        if (clientInfo.isConnect) {
                            messageUDP.sendMessage(clientInfo.ip, vo)
                        }
                    }
                }
                editMessage.text.clear()
            }
        }
    }

    private fun setSmallLayout() {
        activity!!.runOnUiThread {
            //            smallLayout.removeAllViews()
        }
    }

    fun expandLayout() {
//        smallScroll?.visibility = ViewGroup.GONE
//        messageLayout?.visibility = ViewGroup.VISIBLE
    }

    fun reduceLayout() {
//        smallScroll?.visibility = ViewGroup.VISIBLE
//        messageLayout?.visibility = ViewGroup.GONE
    }

    fun addMessage(messageVO: MessageVO) {
        messageRecyclerAdapter.addNewMessage(messageVO)
        recyclerMessage?.scrollToPosition(0)
    }
}
