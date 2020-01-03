package stws.chatstocker.view.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import stws.chatstocker.R
import stws.chatstocker.databinding.UserListBinding
import stws.chatstocker.model.ChatMessage
import stws.chatstocker.model.FileDetails
import stws.chatstocker.model.User
import stws.chatstocker.viewmodel.HomeViewModel
import stws.chatstocker.viewmodel.UserListDetailsViewModel
import stws.chatstocker.viewmodel.UserListViewModel
import java.io.File

class UserAdapter(val context: Context, var userList: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.MyViewHolder>() {
    private lateinit var userListBinding: UserListBinding;
    public var url: ArrayList<File>? = null
    var forwardingurl: ArrayList<ChatMessage>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        userListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.user_list, parent, false)
        return MyViewHolder(userListBinding)
    }

    override fun getItemCount(): Int {
        return userList.size;
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItem(userList.get(position))
        Glide.with(context).load(userList.get(position).image).placeholder(R.drawable.logo).into(holder.imageView)
    }

    fun updateList(updateduserList: ArrayList<User>) {
        userList = updateduserList;
        notifyDataSetChanged()
    }

    fun add(user: User) {
        userList.add(user)
        notifyDataSetChanged()
    }

    fun setExternalFileUrl(url: ArrayList<File>) {
        this.url = url
    }
    fun setForwardingUrl(forwardingurl:ArrayList<ChatMessage>){
        this.forwardingurl=forwardingurl
    }

    inner class MyViewHolder(val userListBinding: UserListBinding) : RecyclerView.ViewHolder(userListBinding.root) {
        val imageView = userListBinding.imgUserPic

        public fun bindItem(user: User) {
            var userViewModel:UserListDetailsViewModel
            if (userListBinding.getViewModel() == null) {
                 userViewModel=UserListDetailsViewModel(user)
                userListBinding.setViewModel(userViewModel)
            }
            else{
                userListBinding.viewModel!!.user=user
            }
            if ( url!=null)
            userListBinding?.viewModel?.externalUrl = url!!
            else if (forwardingurl!=null)
                userListBinding?.viewModel?.forwardingurl = forwardingurl!!

//            } else
//                userListBinding!!.viewModel!!.user = user
        }
    }
}