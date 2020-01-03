package stws.chatstocker.view.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
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
import stws.chatstocker.utils.DateTimeUtils
import stws.chatstocker.utils.FileSingleton
import stws.chatstocker.viewmodel.PhotoViewModel
import java.io.ByteArrayOutputStream


class PhotoAdapter(val contxet:AppCompatActivity,val diveService: Drive,val list: MutableList<FileDetails>,val isVideo:Boolean,val fileSelectedListener: FileSelectedListener) : RecyclerView.Adapter<PhotoAdapter.MyViewHolder>(),PhotoViewModel.FileDeletedListener {
    lateinit var photoListBinding: PhotoListBinding
     var selectedList: ArrayList<FileDetails> = ArrayList()

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

    override fun fileDeleted(pos: FileDetails) {
        super.fileDeleted(pos)
        list.remove(pos)
        notifyDataSetChanged()
    }
    inner class MyViewHolder( itemView: PhotoListBinding) : RecyclerView.ViewHolder(itemView.root) ,FileDownloadProgressListener{
        override fun onProgress(value: Float) {
            progressBar.progress=value
        }

        val imageView=itemView.imageView
        val progressBar=itemView.pgbProgress
        val checkBox=itemView.chkbok;

        lateinit var progressListener:FileDownloadProgressListener
        var  createdtime:String=""
        var  pos=0;

        fun bindItem(photos:FileDetails){
//            if (createdtime.equals(DateTimeUtils.convertDateTimetoDay(photos.createdTime,"dd MMM")))
             createdtime=DateTimeUtils.convertDateTimetoDay(photos.createdTime,"dd MMM")
            val photo= java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Chatstocker")
            var photoFile= java.io.File(photo.path + java.io.File.separator
                    + photos.fileId + "." + "mp4")
            progressListener=this
            Glide.with(imageView.context).load(photos.thumbnail).into(imageView)
            val photoViewModel= PhotoViewModel(photos,photos.thumbnail!!,isVideo,photos.fileId!!,diveService,progressListener,adapterPosition,this@PhotoAdapter)

            photoListBinding?.viewModel?.date=createdtime
            photoViewModel.context=contxet
            if (isVideo) {
                progressBar.visibility = View.VISIBLE
                if (photoFile.exists())
                photoViewModel.isDownloaded!!.set(true)
            }
            else {
                photoFile= java.io.File(photo.path + java.io.File.separator
                        + photos.fileId + "." + "jpg")
                if (photoFile.exists()) {
                    photoViewModel.isDownloaded!!.set(true)

                }
//                progressBar.visibility = View.GONE
            }
            photoListBinding.viewModel=photoViewModel
            checkBox.setOnCheckedChangeListener(object :CompoundButton.OnCheckedChangeListener{
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    if (isChecked)
                        selectedList.add(photos)
                    else
                        selectedList.remove(photos)
                    if (fileSelectedListener!=null)
                        fileSelectedListener.selectedFile(selectedList)
                }


            })




//            else
//                photoListBinding.viewModel!!.setPhotos(photos)

        }

    }
    public interface FileDownloadProgressListener{
        public fun onProgress( value:Float)



    }
    interface FileSelectedListener{
        public fun selectedFile(photos: ArrayList<FileDetails>)
    }


}