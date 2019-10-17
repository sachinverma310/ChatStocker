package stws.chatstocker.viewmodel

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import stws.chatstocker.ConstantsValues
import stws.chatstocker.model.Photos
import stws.chatstocker.view.FullscreenImageActivity

class PhotoViewModel(val url: String):ViewModel(),ConstantsValues {

    fun openfullScreenImage(view:View){
        val intent=Intent(view.context,FullscreenImageActivity::class.java)
        intent.putExtra(ConstantsValues.KEY_FILE_URL,url)
        view.context.startActivity(intent)
    }

}