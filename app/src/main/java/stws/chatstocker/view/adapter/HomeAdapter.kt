package stws.chatstocker.view.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.actions.ItemListIntents
import stws.chatstocker.R
import stws.chatstocker.databinding.HomeListBinding
import stws.chatstocker.viewmodel.HomeViewModel

class HomeAdapter(val onItemClcik: OnItemClcik,val context: Context,val list: ArrayList<Drawable>,val homeItemList: Array<String>) : RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {
    private lateinit var homeListBinding:HomeListBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        homeListBinding=DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.home_list,parent,false)

        return MyViewHolder(homeListBinding)
    }

    override fun getItemCount(): Int {
      return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var image=list.get(position);
        var title=homeItemList[position];
            holder.bindItem(image,title)
        holder.itemView.setOnClickListener(View.OnClickListener {
            onItemClcik.onItemClick(position)
        })
    }

    inner class MyViewHolder(itemView: HomeListBinding) : RecyclerView.ViewHolder(itemView.linearLayout2) {
         public fun bindItem(image: Drawable,title:String){
             if (homeListBinding.getViewModel() == null) {
                 homeListBinding.setViewModel(HomeViewModel(image,title,adapterPosition,context))
             } else
                 homeListBinding.setViewModel(HomeViewModel(image,title,adapterPosition,context))
         }

    }

    interface OnItemClcik{
        fun onItemClick(post:Int)
    }
}