package stws.chatstocker.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

import stws.chatstocker.databinding.ContactListBinding
import stws.chatstocker.model.ContactsList
import stws.chatstocker.viewmodel.ContactViewModel

import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import stws.chatstocker.model.User
import stws.chatstocker.viewmodel.ContactDetailsViewModel


class ContactAdapter(var contactList:List<ContactsList>): RecyclerView.Adapter<ContactAdapter.MyViewHolder>(),Filterable {
    private lateinit var contactBinding:ContactListBinding
    private lateinit var contactViewModel:ContactViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        contactBinding=DataBindingUtil.inflate(LayoutInflater.from(parent.context), stws.chatstocker.R.layout.contact_list,parent,false)
        return MyViewHolder(contactBinding)
    }

    override fun getItemCount(): Int {
       return contactList.size
    }
    fun updateList(updateduserList: List<ContactsList>) {
        contactList = updateduserList;
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindItem(contactList.get(position))
    }

    class MyViewHolder(val contactBinding:ContactListBinding):RecyclerView.ViewHolder(contactBinding.root){
        val generator = ColorGenerator.MATERIAL
        fun bindItem(contactList: ContactsList){
            val drawable = TextDrawable.builder()
                    .buildRound(contactList.name[0].toString(), generator.getRandomColor())
            contactBinding.imgChar.setImageDrawable(drawable)
//            if (contactBinding.viewModel==null){
                contactBinding.viewModel= ContactDetailsViewModel(contactList)
//            }
//            else{
//                contactBinding!!.viewModel!!.contacts=contactList
//            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(charSequence: CharSequence?, filterResults: Filter.FilterResults) {

                contactList = filterResults.values as List<ContactsList>
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase()

                val filterResults = Filter.FilterResults()
                filterResults.values = if (queryString==null || queryString.isEmpty())
                    contactList
                else
                    contactList.filter {
                        it.name.toLowerCase().contains(queryString)
                    }
                return filterResults
            }
        }
    }
}