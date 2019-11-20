package stws.chatstocker.viewmodel

import androidx.lifecycle.ViewModel
import android.provider.ContactsContract
import android.content.ContentResolver
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import de.ulrichraab.rxcontacts.Contact
import de.ulrichraab.rxcontacts.RxContacts
import stws.chatstocker.model.ContactsList


class ContactViewModel() : ViewModel() {









//    private var contactsList: MutableLiveData<List<ContactsList>>? = null
//    fun getContacts(context: Context): LiveData<List<ContactsList>> {
//        if (contactsList == null) {
//            contactsList = MutableLiveData()
//            getContactList(context)
//        }
//
//        return contactsList as MutableLiveData
//    }
    private var contactsList: MutableLiveData<Contact>? = null
    fun getContacts(context: Context): LiveData<Contact> {
        if (contactsList == null) {
            contactsList = MutableLiveData()
            getContactList(context)
        }

        return contactsList as MutableLiveData
    }

    private fun getContactList(context: Context) {
        val cr = context.getContentResolver()
        val cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        val list = ArrayList<ContactsList>()
//        if ((if (cur != null) cur!!.getCount() else 0) > 0) {
//            while (cur != null && cur!!.moveToNext()) {
//                val id = cur!!.getString(
//                        cur!!.getColumnIndex(ContactsContract.Contacts._ID))
//                val name = cur!!.getString(cur!!.getColumnIndex(
//                        ContactsContract.Contacts.DISPLAY_NAME))
//
//                if (cur!!.getInt(cur!!.getColumnIndex(
//                                ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
//                    val pCur = cr.query(
//                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
//                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//                            arrayOf<String>(id), null)
//                    while (pCur!!.moveToNext()) {
//                        val phoneNo = pCur!!.getString(pCur!!.getColumnIndex(
//                                ContactsContract.CommonDataKinds.Phone.NUMBER))
//                        val contactsList = ContactsList(name, phoneNo,name[0].toString())
//                        list.add(contactsList)
//
//                    }
//                    pCur!!.close()
//                }
//            }
//            contactsList!!.postValue(list)
//        }
//        if (cur != null) {
//            cur!!.close()
//        }
        RxContacts.fetch(context)
                .subscribe(object :rx.Observer<Contact>{
                    override fun onError(e: Throwable?) {

                    }

                    override fun onNext(t: Contact?) {
                       contactsList!!.postValue(t)
                    }

                    override fun onCompleted() {

                    }


                })
    }
}