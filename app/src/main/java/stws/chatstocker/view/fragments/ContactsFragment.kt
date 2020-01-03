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

import ir.mirrajabi.rxcontacts.Contact
import ir.mirrajabi.rxcontacts.RxContacts
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
        getContactsListings()


    }
    private fun getContactsListing(){
      val  contactList=ArrayList<String>()
        val contacts = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        // Empty text view


        // Loop through the contacts
        while (contacts!!.moveToNext())
        {
            // Get the current contact name
            val name = contacts.getString(
                    contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));

            // Get the current contact phone number
            val phoneNumber = contacts.getString(
                    contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim();
            if (phoneNumber.contains("+91"))
                contactList.add(phoneNumber.replace(" ",""))
            else
                contactList.add("+91"+phoneNumber.replace(" ",""))
            // Display the contact to text view



        }
        contacts.close();
    }
    private fun getContactsListings() {
      val  contactList = ArrayList<String>()
        val contacts = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        // Empty text view


        // Loop through the contacts
        while (contacts!!.moveToNext()) {
            // Get the current contact name
            val displayName = contacts.getString(
                    contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));

            // Get the current contact phone number
            val phoneNumber = contacts.getString(
                    contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim();
//            val name = contacts.getString(
//                    contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).trim();
            if (phoneNumber.contains("+91"))
                contactList.add(phoneNumber.replace(" ", ""))
            else
                contactList.add("+91" + phoneNumber.replace(" ", ""))
            // Display the contact to text view
            val contactsList = ContactsList(displayName, phoneNumber,displayName[0].toString())
            list.add(contactsList)
            adapter.notifyDataSetChanged()

        }

        contacts.close();
    }
}