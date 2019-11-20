package stws.chatstocker.view.adapter

import android.content.Intent
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.api.services.drive.Drive
import stws.chatstocker.ConstantsValues
import stws.chatstocker.ConstantsValues.KEY_FILE
import stws.chatstocker.ConstantsValues.KEY_PATH
import stws.chatstocker.R
import stws.chatstocker.databinding.AudioListBinding
import stws.chatstocker.databinding.PhotoListBinding
import stws.chatstocker.model.FileDetails
import stws.chatstocker.utils.DateTimeUtils
import stws.chatstocker.view.AudioPalyerActivity
import stws.chatstocker.viewmodel.AudioViewModel

class AudioAdapter (val list: MutableList<FileDetails>,val diveService: Drive,val fileSelectedListener: FileSelectedListener) : RecyclerView.Adapter<AudioAdapter.MyViewHolder>(),AudioViewModel.FileDeletedListener {
    var selectedList: ArrayList<FileDetails> = ArrayList()
    override fun fileDeleted(pos: Int) {
        list.removeAt(pos)
        notifyDataSetChanged()
    }

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

    inner class MyViewHolder( itemView: AudioListBinding) : RecyclerView.ViewHolder(itemView.root), FileDownloadProgressListener {
        override fun onProgress(value: Float) {

        }

        val tvName=itemView.tvName
        lateinit var progressListener: FileDownloadProgressListener
        val progressBar=itemView.pgbProgress
        val checkBox=itemView.chkbok;
        fun bindItem(photos:FileDetails){
            val audio= java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Chatstocker/Audio")
            val photoFile= java.io.File(audio.path + java.io.File.separator
                    + photos.thumbnail)
            progressListener=this
            val photoViewModel= AudioViewModel(photos.thumbnail!!,photos.fileId!!,photos.thumbnail,diveService,progressListener,adapterPosition,this@AudioAdapter)
            photoListBinding.viewModel=photoViewModel
            photoListBinding?.viewModel?.date= DateTimeUtils.convertDateTimetoDay(photos.createdTime,"dd MMM")
            photoViewModel.context=tvName.context

            progressBar.visibility = View.VISIBLE
            if (photoFile.exists())
                photoViewModel.isDownloaded!!.set(true)
            tvName.text=photos.thumbnail
            itemView.setOnClickListener(View.OnClickListener {
                if (photoFile.exists()){
                    val intent=Intent(progressBar.context,AudioPalyerActivity::class.java)
                    intent.putExtra(KEY_PATH,photoFile.absolutePath)
                    intent.putExtra(KEY_FILE,photos)
                    ( progressBar.context as AppCompatActivity).startActivityForResult(intent, ConstantsValues.KEY_FULL_SCREEN_REQUEST_CODE)
                }
            })
            checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    if (isChecked)
                        selectedList.add(photos)
                    else
                        selectedList.remove(photos)
                    if (fileSelectedListener!=null)
                        fileSelectedListener.selectedFile(selectedList)
                }


            })
//            Glide.with(imageView.context).load(photos).into(imageView)
//            imageView.setImageBitmap(photos)
//            if (photoListBinding.viewModel==null)
//                photoListBinding.viewModel= PhotoViewModel(photos)
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