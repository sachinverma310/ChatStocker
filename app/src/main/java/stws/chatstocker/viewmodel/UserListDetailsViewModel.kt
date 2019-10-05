package stws.chatstocker.viewmodel

import android.content.Intent
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import stws.chatstocker.ConstantsValues
import stws.chatstocker.ConstantsValues.KEYOTHER_UID
import stws.chatstocker.model.User
import stws.chatstocker.view.ChatActivity

class UserListDetailsViewModel(user: User) : ViewModel(),ConstantsValues {
    var user: User? = user
        get() = field
        set(value) {
            field = value
        }
    var email: String?
    var name: String?
    var phone: String?
    var lastSeen: String?
    var uid: String=""
    var imgUrl: String = ""

    //    set(value) {
//        field=value
//    }
//    get()=field


    var onlineStatus: String?

    init {
        this.user = user

        this.email = user!!.email
        this.phone = user!!.email
        this.name = user!!.name
        this.lastSeen = user!!.lastSeen
        this.onlineStatus = user!!.online.toString()
        this.uid= user!!.uid.toString();
        this.imgUrl=user!!.image.toString()
    }

    fun onUserClick(view:View){
        val intent=Intent(view.context,ChatActivity::class.java)
        intent.putExtra(KEYOTHER_UID,user)
        view.context.startActivity(intent)
    }


}