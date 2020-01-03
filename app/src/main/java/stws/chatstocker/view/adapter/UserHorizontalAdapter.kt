package stws.chatstocker.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import stws.chatstocker.R
import stws.chatstocker.databinding.UserListBinding
import stws.chatstocker.databinding.UserListHorzBinding
import stws.chatstocker.model.User
import stws.chatstocker.viewmodel.UserListDetailsViewModel

class UserHorizontalAdapter (val context: Context, var user: User,var itemSelectedListener: GroupUserAdapter.ItemSelectedListener) : RecyclerView.Adapter<UserHorizontalAdapter.MyViewHolder>() {
    private lateinit var userListBinding: UserListHorzBinding;
    private  var userList=ArrayList<User>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        userListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.user_list_horz, parent, false)
        return MyViewHolder(userListBinding)
    }

    override fun getItemCount(): Int {
        return userList.size;
    }
    fun addUser(user: User){
        userList.add(user)
        notifyDataSetChanged()
    }
    fun removeUser(user: User){
        userList.remove(user)
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItem(userList.get(position)!!)
        Glide.with(context).load(userList.get(position)!!.image).placeholder(R.drawable.logo).into(holder.imageView)
        holder.imgDel.setOnClickListener(View.OnClickListener {
            userList.remove(userList.get(position))
            notifyDataSetChanged()
            itemSelectedListener.onItemUnSelected(position)
        })

    }

//    fun updateList(updateduserList:User) {
//        userList = updateduserList;
//        notifyDataSetChanged()
//    }

    public class MyViewHolder(val userListBinding: UserListHorzBinding) : RecyclerView.ViewHolder(userListBinding.root) {
        val imageView = userListBinding.profilePic
        val imgDel=userListBinding.imgDelete;
        public fun bindItem(user: User) {
            if (userListBinding.getViewModel() == null) {
                userListBinding.setViewModel(UserListDetailsViewModel(user))
            } else
                userListBinding!!.viewModel!!.user = user
        }
    }


}