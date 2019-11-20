package stws.chatstocker.view.fragments

import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import de.ulrichraab.rxcontacts.Contact
import de.ulrichraab.rxcontacts.RxContacts
import stws.chatstocker.R
import stws.chatstocker.databinding.ContactsFragmentBinding
import stws.chatstocker.model.ContactsList
import stws.chatstocker.view.BaseActivity
import stws.chatstocker.view.adapter.ContactAdapter
import stws.chatstocker.viewmodel.ContactViewModel

class ContactsFragment: BaseActivity() {
    lateinit var list: MutableList<ContactsList>
lateinit var   adapter:ContactAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contactsFragmentBinding=DataBindingUtil.inflate<ContactsFragmentBinding>(layoutInflater, R.layout.contacts_fragment,frameLayout,true)
        val recyclerView=contactsFragmentBinding.recyclerView
        val contactViewModel=ViewModelProviders.of(this).get(ContactViewModel::class.java)
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.itemAnimator=DefaultItemAnimator()
        list=ArrayList()
         adapter=ContactAdapter(list)
        recyclerView.adapter=adapter
//        val contactsList = ContactsList(name, phoneNo,name[0].toString())
//                        list.add(contactsList)
//         contactViewModel.getContacts(this).observe(this, Observer {
//             val contactsList = ContactsList(it.displayName, it.phoneNumbers.toString(),it.displayName[0].toString())
//                list.add(contactsList)
//             adapter.notifyDataSetChanged()
//        })
        getContactList()


    }

    private fun getContactList() {
//        val cr = context.getContentResolver()
//        val cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
//        val list = ArrayList<ContactsList>()
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
        RxContacts.fetch(this)
                .subscribe(object :rx.Observer<Contact>{
                    override fun onError(e: Throwable?) {

                    }

                    override fun onNext(it: Contact?) {
                        val contactsList = ContactsList(it!!.displayName, it!!.phoneNumbers.elementAt(0),it!!.displayName[0].toString())
                        list.add(contactsList)
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCompleted() {

                    }


                })
    }
}