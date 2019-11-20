package stws.chatstocker.viewmodel

import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.lifecycle.ViewModel
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.google.firebase.database.FirebaseDatabase
import stws.chatstocker.ConstantsValues
import stws.chatstocker.utils.Prefrences
import stws.chatstocker.view.FullProfilePicViewrActivity
import java.io.IOException

class ProfileViewModel() : ViewModel() {
    var name: String = ""
    var phone: String = ""
    var email: String = ""
    var lastPhone: String = ""

    constructor(name: String, phone: String, email: String) : this() {
        this.name = name
        this.phone = phone
        this.email = email
        this.lastPhone = phone;
    }

    //    fun updateClick(view:View){
//
//            if (lastPhone.equals(phone)){
//                    FirebaseDatabase.getInstance().reference.child("User").child(Prefrences.getUserUid(view.context)!!).child("name")
//                            .setValue(name)
//            }
//
//    }
    fun onProfileClick(view: View) {
        val imagePopup =  ImagePopup(view.context);
        imagePopup.setWindowHeight(500); // Optional
        imagePopup.setWindowWidth(500); // Optional
        imagePopup.setBackgroundColor(Color.BLACK);  // Optional
        imagePopup.setFullScreen(true); // Optional
        imagePopup.setHideCloseIcon(true);  // Optional
        imagePopup.setImageOnClickClose(true);
        try {
            imagePopup.initiatePopupWithGlide(Prefrences.getUserDetails(view.context, ConstantsValues.KEY_LOGIN_DATA).profile)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        imagePopup.viewPopup()
//        val intent = Intent(view.context, FullProfilePicViewrActivity::class.java)
//        intent.putExtra(ConstantsValues.KEY_USER_ID, "")
//        try {
//            intent.putExtra(ConstantsValues.KEY_FILE_URL, Prefrences.getUserDetails(view.context, ConstantsValues.KEY_LOGIN_DATA).profile)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        } catch (e: ClassNotFoundException) {
//            e.printStackTrace()
//        }
//
//        view.context.startActivity(intent)

    }
}