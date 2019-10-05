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
import stws.chatstocker.model.User
import stws.chatstocker.viewmodel.HomeViewModel
import stws.chatstocker.viewmodel.UserListDetailsViewModel
import stws.chatstocker.viewmodel.UserListViewModel

class UserAdapter(val context:Context,val userList:ArrayList<User>): RecyclerView.Adapter<UserAdapter.MyViewHolder>() {
    private lateinit var userListBinding:UserListBinding;
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        userListBinding=DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.user_list,parent,false)
        return MyViewHolder(userListBinding)
    }

    override fun getItemCount(): Int {
     return userList.size;
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItem(userList.get(position))
        Glide.with(context).load(userList.get(position).image).into(holder.imageView)
    }

    public class MyViewHolder(val userListBinding:UserListBinding):RecyclerView.ViewHolder(userListBinding.root){
        val imageView=userListBinding.imgUserPic
        public fun bindItem(user:User){
            if (userListBinding.getViewModel() == null) {
                userListBinding.setViewModel(UserListDetailsViewModel(user))
            } else
                userListBinding!!.viewModel!!.user=user
        }
    }
}