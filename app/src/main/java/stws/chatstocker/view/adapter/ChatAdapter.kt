package stws.chatstocker.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import stws.chatstocker.R
import stws.chatstocker.databinding.OtherChatLayoutBinding
import stws.chatstocker.databinding.OwnChatLayoutBinding
import stws.chatstocker.model.ChatMessage

import stws.chatstocker.viewmodel.MessageViewModel
import android.text.method.TextKeyListener.clear
import com.bumptech.glide.Glide


private const val VIEW_TYPE_MY_MESSAGE = 1
private const val VIEW_TYPE_OTHER_MESSAGE = 2

class ChatAdapter(val context: Context,val messages: java.util.ArrayList<ChatMessage>?) : RecyclerView.Adapter<MessageViewHolder>() {
//    private val messages: java.util.ArrayList<ChatMessage?>
    private lateinit var ownChatLayoutBinding:OwnChatLayoutBinding
    private lateinit var otherChatLayoutBinding:OtherChatLayoutBinding
    fun addMessage(message: ChatMessage) {
        messages!!.add(message)
//        val hashSet = HashSet<ChatMessage>()
//        hashSet.addAll(messages)
//        messages.clear()
//        messages.addAll(hashSet)
        notifyItemInserted(messages.size-1)
    }
    fun getList():java.util.ArrayList<ChatMessage>? {
       return messages

    }
    override fun getItemCount(): Int {
        return messages!!.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages!!.get(position)

        val cuurentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        return if (messages.get(position)!!.from.equals(cuurentUserId))
            VIEW_TYPE_MY_MESSAGE
        else
            VIEW_TYPE_OTHER_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if (viewType == VIEW_TYPE_MY_MESSAGE) {
           ownChatLayoutBinding= DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.own_chat_layout,parent,false)
            MyMessageViewHolder(ownChatLayoutBinding)
        } else {
             otherChatLayoutBinding= DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.other_chat_layout,parent,false)
            OtherMessageViewHolder(otherChatLayoutBinding)
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages!!.get(position)

        holder?.bind(message)
    }

    inner class MyMessageViewHolder( val ownChatLayoutBinding: OwnChatLayoutBinding) : MessageViewHolder(ownChatLayoutBinding.root) {
//        private var messageText: TextView = view.txtMyMessage
//        private var timeText: TextView = view.txtMyMessageTime

        override fun bind(message: ChatMessage?) {

            if (message!!.type.equals("image")) {
                ownChatLayoutBinding.imageLayout.visibility = View.VISIBLE
                Glide.with(context).load(message!!.msg).into(ownChatLayoutBinding.imgFile)
                ownChatLayoutBinding.chatLayout.visibility = View.GONE
            }
            else {
                ownChatLayoutBinding.imageLayout.visibility = View.GONE
                ownChatLayoutBinding.chatLayout.visibility = View.VISIBLE
            }
            if (ownChatLayoutBinding.viewModel==null)
                ownChatLayoutBinding.setViewModel(MessageViewModel(message))
            else
                ownChatLayoutBinding!!.viewModel!!.chatMessage=message
//            messageText.text = message.message
//            timeText.text = DateUtils.fromMillisToTimeString(message.time)
        }

    }

    inner class OtherMessageViewHolder(val othetChatLayoutBinding: OtherChatLayoutBinding) : MessageViewHolder(othetChatLayoutBinding.root) {
//        private var messageText: TextView = view.txtOtherMessage
//        private var userText: TextView = view.txtOtherUser
//        private var timeText: TextView = view.txtOtherMessageTime

        override fun bind(message: ChatMessage?) {
            Glide.with(context).load(message!!.msg).into(othetChatLayoutBinding.imgFile)
            if (message!!.type.equals("image")) {
                othetChatLayoutBinding.imageLayout.visibility = View.VISIBLE
                Glide.with(context).load(message!!.msg).into(othetChatLayoutBinding.imgFile)
                othetChatLayoutBinding.constraintLayout2.visibility = View.GONE
            }
            else {
                othetChatLayoutBinding.imageLayout.visibility = View.GONE
                othetChatLayoutBinding.constraintLayout2.visibility = View.VISIBLE
            }
            if (othetChatLayoutBinding.viewModel==null)
                othetChatLayoutBinding.setViewModel(MessageViewModel(message))
            else
                othetChatLayoutBinding!!.viewModel!!.chatMessage=message
//            messageText.text = message.message
//            userText.text = message.user
//            timeText.text = DateUtils.fromMillisToTimeString(message.time)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}

open class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(message: ChatMessage?) {

    }


}
