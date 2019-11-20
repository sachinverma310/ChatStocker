package stws.chatstocker.viewmodel

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import stws.chatstocker.view.ProfileActivity

class SettingViewModel:ViewModel() {

    fun accountClick(view:View){
        view.context.startActivity(Intent(view.context,ProfileActivity::class.java))
    }
    fun helpClick(view:View){
        view.context.startActivity(Intent(view.context,ProfileActivity::class.java))
    }
    fun privacyClick(view:View){
        view.context.startActivity(Intent(view.context,ProfileActivity::class.java))
    }
//    fun logout(view:View){
//        view.context.startActivity(Intent(view.context,ProfileActivity::class.java))
//    }
}