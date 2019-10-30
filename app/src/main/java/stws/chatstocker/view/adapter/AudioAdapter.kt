package stws.chatstocker.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import stws.chatstocker.R
import stws.chatstocker.databinding.AudioListBinding
import stws.chatstocker.databinding.PhotoListBinding
import stws.chatstocker.model.FileDetails

class AudioAdapter (val list: List<FileDetails>) : RecyclerView.Adapter<AudioAdapter.MyViewHolder>() {
    lateinit var photoListBinding: AudioListBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        photoListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.audio_list, parent, false)
        return MyViewHolder(photoListBinding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItem(list.get(position))

    }

    inner class MyViewHolder( itemView: AudioListBinding) : RecyclerView.ViewHolder(itemView.root) {
        val tvName=itemView.tvName
        fun bindItem(photos:FileDetails){
            tvName.text=photos.thumbnail
//            Glide.with(imageView.context).load(photos).into(imageView)
//            imageView.setImageBitmap(photos)
//            if (photoListBinding.viewModel==null)
//                photoListBinding.viewModel= PhotoViewModel(photos)
//            else
//                photoListBinding.viewModel!!.setPhotos(photos)

        }
    }
}