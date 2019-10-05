package stws.chatstocker.viewmodel

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import stws.chatstocker.model.ChatMessage
import stws.chatstocker.utils.DateTimeUtils

class MessageViewModel(chatMessage: ChatMessage?) : ViewModel() {
    var chatMessage: ChatMessage? = chatMessage
        get() = field
        set(value) {
            field = value
        }
    var msg: String


    var date: String
//    var imgUrl:String
    init {
        this.chatMessage = chatMessage
        msg = chatMessage!!.msg
//        imgUrl=chatMessage!!.msg
//        date ="12/10"
        date = DateTimeUtils.convertMillisecondtodate(chatMessage!!.date.toLong(), "hh:mm a")
    }


}