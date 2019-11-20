package stws.chatstocker.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import stws.chatstocker.R
import stws.chatstocker.databinding.GroupUserListBinding
import stws.chatstocker.databinding.UserListBinding
import stws.chatstocker.model.User
import stws.chatstocker.viewmodel.GroupUserViewModel
import stws.chatstocker.viewmodel.UserListDetailsViewModel

class GroupUserAdapter (val context: Context, var userList: ArrayList<User>,var itemSelectedListener:ItemSelectedListener) : RecyclerView.Adapter<GroupUserAdapter.MyViewHolder>() {
    private lateinit var userListBinding: GroupUserListBinding;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        userListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.group_user_list, parent, false)
        return MyViewHolder(userListBinding)
    }

    override fun getItemCount(): Int {
        return userList.size;
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItem(userList.get(position))
        holder.itemView.setBackgroundColor(if (userList.get(position).isSeletced!!) Color.GRAY else Color.TRANSPARENT)
        Glide.with(context).load(userList.get(position).image).into(holder.imageView)
        holder.itemView.setOnClickListener(View.OnClickListener {
            userList.get(position).isSeletced = !userList.get(position).isSeletced!!
            holder.itemView.setBackgroundColor(if (userList.get(position).isSeletced!!) Color.GRAY else Color.TRANSPARENT)
            if (userList.get(position).isSeletced!!)
                itemSelectedListener.onItemSelected(position,false)
            else
                itemSelectedListener.onItemSelected(position,true)
        })
    }

    fun updateList(updateduserList: ArrayList<User>) {
        userList = updateduserList;
        notifyDataSetChanged()
    }

    public class MyViewHolder(val userListBinding: GroupUserListBinding) : RecyclerView.ViewHolder(userListBinding.root) {
        val imageView = userListBinding.imgUserPic
        public fun bindItem(user: User) {
//            if (userListBinding.getViewModel() == null)
                userListBinding.setViewModel(GroupUserViewModel(user))
//            } else
//                userListBinding!!.viewModel!!.user = GroupUserViewModel(user)
        }
    }
    public interface ItemSelectedListener {
        fun onItemSelected(position: Int,isRemoved: Boolean)
        fun onItemUnSelected(position: Int)

    }
}