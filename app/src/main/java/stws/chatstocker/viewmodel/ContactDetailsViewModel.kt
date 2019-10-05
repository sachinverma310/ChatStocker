package stws.chatstocker.viewmodel

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
    init {
        this.contacts = contacts
        this.name=contacts!!.name
        this.phone=contacts!!.number
    }
}