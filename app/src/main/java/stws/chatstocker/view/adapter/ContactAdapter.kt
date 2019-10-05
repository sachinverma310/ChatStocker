package stws.chatstocker.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

import stws.chatstocker.databinding.ContactListBinding
import stws.chatstocker.model.ContactsList
import stws.chatstocker.viewmodel.ContactViewModel

import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import stws.chatstocker.viewmodel.ContactDetailsViewModel


class ContactAdapter(val contactList:List<ContactsList>): RecyclerView.Adapter<ContactAdapter.MyViewHolder>() {
    private lateinit var contactBinding:ContactListBinding
    private lateinit var contactViewModel:ContactViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        contactBinding=DataBindingUtil.inflate(LayoutInflater.from(parent.context), stws.chatstocker.R.layout.contact_list,parent,false)
        return MyViewHolder(contactBinding)
    }

    override fun getItemCount(): Int {
       return contactList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindItem(contactList.get(position))
    }

    class MyViewHolder(val contactBinding:ContactListBinding):RecyclerView.ViewHolder(contactBinding.root){
        val generator = ColorGenerator.MATERIAL
        fun bindItem(contactList: ContactsList){
            val drawable = TextDrawable.builder()
                    .buildRound(contactList.name, generator.getRandomColor())
            contactBinding.imgChar.setImageDrawable(drawable)
            if (contactBinding.viewModel==null){
                contactBinding.viewModel= ContactDetailsViewModel(contactList)
            }
            else{
                contactBinding!!.viewModel!!.contacts=contactList
            }
        }
    }
}