package stws.chatstocker.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import stws.chatstocker.R
import stws.chatstocker.databinding.ContactsFragmentBinding
import stws.chatstocker.view.BaseActivity
import stws.chatstocker.view.adapter.ContactAdapter
import stws.chatstocker.viewmodel.ContactViewModel

class ContactsFragment: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contactsFragmentBinding=DataBindingUtil.inflate<ContactsFragmentBinding>(layoutInflater, R.layout.contacts_fragment,frameLayout,true)
        val recyclerView=contactsFragmentBinding.recyclerView
        val contactViewModel=ViewModelProviders.of(this).get(ContactViewModel::class.java)
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.itemAnimator=DefaultItemAnimator()
         contactViewModel.getContacts(this).observe(this, Observer {
            recyclerView.adapter=ContactAdapter(it)
        })


    }
}