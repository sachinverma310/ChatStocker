package stws.chatstocker.view.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import stws.chatstocker.R
import stws.chatstocker.databinding.PhotoListBinding
import stws.chatstocker.model.Photos
import stws.chatstocker.viewmodel.PhotoViewModel
import java.io.ByteArrayOutputStream


class PhotoAdapter(val list: List<String>) : RecyclerView.Adapter<PhotoAdapter.MyViewHolder>() {
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
        val imageView=itemView.imageView
        fun bindItem(photos:String){
            Glide.with(imageView.context).load(photos).into(imageView)

//            imageView.setImageBitmap(photos)
//            if (photoListBinding.viewModel==null)
                photoListBinding.viewModel= PhotoViewModel(photos)
//            else
//                photoListBinding.viewModel!!.setPhotos(photos)

        }
    }
}