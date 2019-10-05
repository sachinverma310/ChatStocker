package stws.chatstocker.viewmodel

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class HomeViewModel() : ViewModel() {
    var image: Drawable?=null
    var title: String?=null
    var pos:Int?=18
    var contex:Context?=null
    var photoFile: File?=null
    var fileUri: Uri?=null
    constructor(image: Drawable, title: String,  pos: Int,  contex: Context):this(){
        this.image = image
        this.title = title
        this.pos=pos
        this.contex=contex

    }

    var file: MutableLiveData<File>? = MutableLiveData()
    fun getFile(): LiveData<File> {


        return file!!
    }

    private val KEY_REQUEST_CODE = 1

    init {


    }

    //    fun setImage() {
//        this.image = image
//    }
//
//    fun getImages(): Drawable {
//        return image;
//    }
//
//fun setTitle(){
//    this.title=title
//}
//    fun getTitles():String{
//        return title
//    }


    @BindingAdapter("android:src")
    fun setImageViewResource(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }

    fun onItemClick(view: View) {


    }

    fun getOutputMediaFileUri(file: File): Uri {
        return FileProvider.getUriForFile(contex!!, contex!!.getPackageName() + ".provider", file)
    }

    private fun getOutputMediaFile(): File? {
        val mediaStorageDir = File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Chatstocker")
        //        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        //                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())


        return File(mediaStorageDir.path + File.separator
                + "IMG_" + timeStamp + "." + "jpg")
    }


}