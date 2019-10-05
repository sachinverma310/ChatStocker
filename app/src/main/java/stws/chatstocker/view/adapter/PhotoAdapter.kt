package stws.chatstocker.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import stws.chatstocker.R
import stws.chatstocker.databinding.PhotoListBinding
import stws.chatstocker.model.Photos
import stws.chatstocker.viewmodel.PhotoViewModel

class PhotoAdapter(val list: List<Photos>) : RecyclerView.Adapter<PhotoAdapter.MyViewHolder>() {
    lateinit var photoListBinding: PhotoListBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        photoListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.photo_list, parent, false)
        return MyViewHolder(photoListBinding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bindItem(list.get(position))
    }

    inner class MyViewHolder( itemView: PhotoListBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun bindItem(photos:Photos){
//            if (photoListBinding.viewModel==null)
//                photoListBinding.viewModel= PhotoViewModel(photos)
//            else
//                photoListBinding.viewModel!!.setPhotos(photos)

        }
    }
}