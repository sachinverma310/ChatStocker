package stws.chatstocker.view.fragments

import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
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
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.search_bar_layout.*
import kotlinx.android.synthetic.main.user_action_bar.*
import stws.chatstocker.R
import stws.chatstocker.databinding.ContactsFragmentBinding
import stws.chatstocker.model.ContactsList
import stws.chatstocker.model.User
import stws.chatstocker.view.BaseActivity
import stws.chatstocker.view.adapter.ContactAdapter
import stws.chatstocker.viewmodel.ContactViewModel
import java.util.*
import kotlin.collections.ArrayList

class ContactsFragment: BaseActivity() {
    lateinit var list: MutableList<ContactsList>
lateinit var   adapter:ContactAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contactsFragmentBinding=DataBindingUtil.inflate<ContactsFragmentBinding>(layoutInflater, R.layout.contacts_fragment,frameLayout,true)
        userActionBar.visibility = View.VISIBLE
        mainActionBar.visibility = View.GONE
        val recyclerView=contactsFragmentBinding.recyclerView
        val contactViewModel=ViewModelProviders.of(this).get(ContactViewModel::class.java)
        tvTitle.setText("Contacts")
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
        imgMore.visibility=View.GONE
        imgCancel.setOnClickListener(View.OnClickListener {
            actionSearchBar.visibility = View.GONE
            userActionBar.visibility = View.VISIBLE
        })
        imgSearchBar.setOnClickListener(View.OnClickListener {
            actionSearchBar.visibility = View.VISIBLE
            userActionBar.visibility = View.GONE
        })
        getContactsListings()
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.length > 0)
                   adapter.filter.filter(s.toString())
                else
                    adapter.updateList(list);
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

    }
    fun filter(text: String) {
        val temp = ArrayList<ContactsList>();
        for (d: ContactsList in list) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.name.toLowerCase()!!.contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        //update recyclerview
        adapter.updateList(temp);
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
        Collections.sort(list)
        adapter.notifyDataSetChanged()

        contacts.close();
    }
}