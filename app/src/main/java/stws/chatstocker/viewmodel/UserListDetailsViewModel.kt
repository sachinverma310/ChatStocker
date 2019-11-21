package stws.chatstocker.viewmodel

import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ceylonlabs.imageviewpopup.ImagePopup
import stws.chatstocker.ConstantsValues
import stws.chatstocker.ConstantsValues.*
import stws.chatstocker.model.ChatMessage
import stws.chatstocker.model.FileDetails
import stws.chatstocker.model.User
import stws.chatstocker.utils.DateTimeUtils
import stws.chatstocker.utils.Prefrences
import stws.chatstocker.view.ChatActivity
import stws.chatstocker.view.FullProfilePicViewrActivity
import stws.chatstocker.view.GroupChatActivtiy
import java.io.File
import java.io.IOException
import java.lang.Exception

class UserListDetailsViewModel(user: User) : ViewModel(), ConstantsValues {
    var user: User? = user
        get() = field
        set(value) {
            field = value
        }
    var email: String?
    var name: String?
    var phone: String?
    var lastSeen: String?
    var uid: String = ""
    var imgUrl: String = ""
    var externalUrl:ArrayList<File>?=null
        get() = field
        set(value) {
            field = value
        }
    var forwardingurl:ArrayList<ChatMessage>?=null
        get() = field
        set(value) {
            field = value
        }

    //    set(value) {
//        field=value
//    }
//    get()=field


    var onlineStatus: String?
    var online:Boolean?
    init {
        this.user = user

        this.email = user!!.email
        this.phone = user!!.email
        this.name = user!!.name
        this.lastSeen = user!!.lastSeen
        this.online=user.online
        this.onlineStatus = user!!.online.toString()
        this.uid = user!!.uid.toString();
        this.imgUrl = user!!.image.toString()
    }

    fun onUserClick(view: View) {
        if (user?.isGroup!!){
            val intent = Intent(view.context, GroupChatActivtiy::class.java)
            intent.putExtra(KEYOTHER_UID, user)
            intent.putExtra(KEY_FILE_URL, externalUrl)
            intent.putExtra(KEY_URL_LIST,forwardingurl)
            view.context.startActivity(intent)
            if (externalUrl!=null)
                (view.context as AppCompatActivity).finish()
        }
        else {
            val intent = Intent(view.context, ChatActivity::class.java)
            intent.putExtra(KEYOTHER_UID, user)
            intent.putExtra(KEY_FILE_URL,externalUrl)
            intent.putExtra(KEY_URL_LIST,forwardingurl)
            view.context.startActivity(intent)
            if (externalUrl!=null)
                (view.context as AppCompatActivity).finish()
        }
    }
    fun convertMillistoTime():String{
        try {
            lastSeen!!.toLong()
            val date= DateTimeUtils.convertDateTimetoDay(lastSeen!!.toLong() ,"yyyy-MM-dd hh:mm")
            return date;
        }
        catch (e: Exception){
            return lastSeen!!
        }


    }
fun profilePicClick(view: View){
    val imagePopup =  ImagePopup(view.context);
    imagePopup.setWindowHeight(500); // Optional
    imagePopup.setWindowWidth(500); // Optional
    imagePopup.setBackgroundColor(Color.BLACK);  // Optional
    imagePopup.setFullScreen(true); // Optional
    imagePopup.setHideCloseIcon(true);  // Optional
    imagePopup.setImageOnClickClose(true);
    try {
        imagePopup.initiatePopupWithGlide(imgUrl)
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }

    imagePopup.viewPopup()
}

}