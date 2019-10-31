package stws.chatstocker.view.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import stws.chatstocker.R
import stws.chatstocker.data.rest.RepoService
import stws.chatstocker.databinding.PhotoListBinding
import stws.chatstocker.model.FileDetails
import stws.chatstocker.model.Photos
import stws.chatstocker.viewmodel.PhotoViewModel
import java.io.ByteArrayOutputStream


class PhotoAdapter(val contxet:AppCompatActivity,val diveService: Drive,val list: List<FileDetails>,val isVideo:Boolean) : RecyclerView.Adapter<PhotoAdapter.MyViewHolder>() {
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

    inner class MyViewHolder( itemView: PhotoListBinding) : RecyclerView.ViewHolder(itemView.root) ,FileDownloadProgressListener{
        override fun onProgress(value: Float) {
            progressBar.progress=value
        }

        val imageView=itemView.imageView
        val progressBar=itemView.pgbProgress
        lateinit var progressListener:FileDownloadProgressListener


        fun bindItem(photos:FileDetails){
            val photo= java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Chatstocker")
            val photoFile= java.io.File(photo.path + java.io.File.separator
                    + photos.fileId + "." + "mp4")
            progressListener=this
            Glide.with(imageView.context).load(photos.thumbnail).into(imageView)
            val photoViewModel= PhotoViewModel(photos.thumbnail,isVideo,photos.fileId,diveService,progressListener)
            photoListBinding.viewModel=photoViewModel

            if (isVideo) {
                progressBar.visibility = View.VISIBLE
                if (photoFile.exists())
                photoViewModel.isDownloaded!!.set(true)
            }
            else {
                photoViewModel.isDownloaded!!.set(true)
                progressBar.visibility = View.GONE
            }




//            else
//                photoListBinding.viewModel!!.setPhotos(photos)

        }

    }
    public interface FileDownloadProgressListener{
        public fun onProgress( value:Float)


    }
}