package stws.chatstocker.viewmodel

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.lifecycle.ViewModel
import stws.chatstocker.model.ContactsList

class ContactDetailsViewModel(contacts: ContactsList?):ViewModel() {
    var contacts: ContactsList?= null
        set(value) {
            field = value
        }
        get() = field
    var name: String? = ""
        set(value) {
            field = value
        }
        get() = field
    var phone: String? = ""
        set(value) {
            field = value
        }
        get() = field
    var firstCharName: String? = ""
        set(value) {
            field = value
        }
        get() = field
    init {
        this.contacts = contacts
        this.name=contacts!!.name
        this.phone=contacts!!.number
        this.firstCharName=contacts!!.firstCharName
    }
    fun callClick(view:View){
        val intent = Intent(Intent.ACTION_DIAL)
                intent.setData(Uri.parse("tel:"+phone));
        view.context.startActivity(intent)
    }
}