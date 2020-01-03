package stws.chatstocker.viewmodel

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.api.client.googleapis.media.MediaHttpDownloader
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener
import com.google.api.client.util.DateTime
import com.google.api.services.drive.Drive
import stws.chatstocker.ConstantsValues
import stws.chatstocker.model.Photos
import stws.chatstocker.utils.ProgressBarHandler
import stws.chatstocker.view.FullscreenImageActivity
import stws.chatstocker.view.VideoPlayerActivity
import stws.chatstocker.view.adapter.PhotoAdapter
import stws.chatstocker.view.fragments.UserFragment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import stws.chatstocker.model.FileDetails
import stws.chatstocker.view.ImageViewActivity


class PhotoViewModel : ViewModel, ConstantsValues {
    constructor() {

    }

    var url: String = ""
    var isVideo: Boolean = false

    var fileId: String = ""
    var drive: Drive? = null
    var progressListener: PhotoAdapter.FileDownloadProgressListener? = null
    var pos: Int = 0
    var fileDeletedListener: FileDeletedListener? = null
    var photos: FileDetails? = null


    constructor(photos: FileDetails, url: String, isVideo: Boolean, fileId: String, drive: Drive, progressListener: PhotoAdapter.FileDownloadProgressListener, pos: Int, fileDeletedListener: FileDeletedListener)
            : this() {
        this.url = url
        this.isVideo = isVideo
        this.fileId = fileId
        this.drive = drive
        this.progressListener = progressListener
        this.pos = pos
        this.fileDeletedListener = fileDeletedListener
        this.photos = photos

    }

    var date: String? = null
        set(value) {
            field = value
        }
        get() = field
    val photo = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Chatstocker")
    var photoFile = File(photo.path + File.separator
            + fileId + "." + "mp4")

    var isDownloaded: ObservableField<Boolean>? = ObservableField<Boolean>()
        set(value) {
            field = value
        }
        get() = field
    var list: ArrayList<FileDetails>?=null
        set(value) {
            field = value
        }
        get() = field
    var context: Context? = null
        set(value) {
            field = value
        }
        get() = field

    var fileDeleted: MutableLiveData<Int> = MutableLiveData()
    fun isfileDeleted(): LiveData<Int> {

        return fileDeleted
    }

    fun openfullScreenImage(view: View) {
            if (!isVideo) {
                photoFile = File(photo.path + File.separator
                        + fileId + "." + "jpg")
                if (photoFile.exists()) {

                    val intent = Intent(view.context, ImageViewActivity::class.java)
                    intent.putExtra(ConstantsValues.KEY_FILE_URL, photoFile.absolutePath)
                    intent.putExtra(ConstantsValues.KEY_FILE,photos)
                    intent.putExtra(ConstantsValues.KEY_POS,pos)
                    ( view.context as AppCompatActivity).startActivityForResult(intent,ConstantsValues.KEY_FULL_SCREEN_REQUEST_CODE)
                }
                else
                    startDownloading(view)
            } else {
                if (photoFile.exists()) {
                    val photoFile = File(photo.path + File.separator
                            + fileId + "." + "mp4")

//      val videoView =activityVideoPlayerBinding.surfView
//        //Set MediaController  to enable play, pause, forward, etc options.
//        val mediaController=  MediaController(this);
//        mediaController.setAnchorView(videoView);

                    //Location of Media File
                    var uri = Uri.parse(photoFile.absolutePath)
                    val intent =  Intent(Intent.ACTION_VIEW, uri);
                    intent.setDataAndType(uri, "video/mp4");
                    ( view.context as AppCompatActivity).startActivity(intent);
//                    val intent = Intent(view.context, VideoPlayerActivity::class.java)
//                    intent.putExtra(ConstantsValues.KEY_VIDEO_ID, fileId)
//                    intent.putExtra(ConstantsValues.KEY_FILE,photos)
//                    ( view.context as AppCompatActivity).startActivityForResult(intent,ConstantsValues.KEY_FULL_SCREEN_REQUEST_CODE)
//                    isDownloaded!!.set(true)
                }
                else
                    startDownloading(view)
            }

    }

    fun startDownloading(view: View) {
        if (!isVideo)
            photoFile = File(photo.path + File.separator
                    + fileId + "." + "jpg")
        else
            photoFile = File(photo.path + File.separator
                    + fileId + "." + "mp4")
        if (!photoFile.exists())
            DownloadTask().execute()
        else {
            openfullScreenImage(view)
        }
    }

    inner class DownloadTask : AsyncTask<String, String, String>(), MediaHttpDownloaderProgressListener {
        //            val MEDIA_IN_PROGRESS=101;
//            val MEDIA_IN_PROGRESS_COMPLETED=102;
        override fun progressChanged(downloader: MediaHttpDownloader?) {
            when (downloader!!.getDownloadState()) {
                MediaHttpDownloader.DownloadState.MEDIA_IN_PROGRESS ->
                    publishProgress((downloader.progress * 100).toInt().toString())
                MediaHttpDownloader.DownloadState.MEDIA_COMPLETE -> {

                    publishProgress("100")
                }

            }
        }

        override fun doInBackground(vararg params: String?): String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val request = drive!!.files().get(fileId)
            request.mediaHttpDownloader.setProgressListener(this).chunkSize = 1000000
            request.executeMediaAndDownloadTo(byteArrayOutputStream)

            if (!photo.exists()) {
                photo.mkdir()
            }
//            val outputStream= FileOutputStream(photo).use({ outputStream -> byteArrayOutputStream.writeTo(outputStream) })

            val data = byteArrayOutputStream.toByteArray()
            try {
                val fos = FileOutputStream(photoFile)


                fos.write(data)
                fos.close()
            } catch (e: java.io.IOException) {
                Log.e("PictureDemo", "Exception in photoCallback", e)
            }

            return "100"
        }

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
            Log.e("progress", values[0])

            progressListener!!.onProgress(values[0]!!.toFloat())
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            isDownloaded!!.set(true)
        }

    }

    fun deleteItem(view: View) {
        if (list==null){
            Toast.makeText(view.context,"please make a selection to Delete",Toast.LENGTH_SHORT).show()
            return
        }
        confirmationDialog(view)

//        request.dele
    }

    fun sendItem(view: View) {
//        if (isDownloaded!!.get() == true) {
        var fileType="jpg"
            if (isVideo) {
                fileType="mp4"
                if (photos != null)
                    photoFile = File(photo.path + File.separator
                            + photos!!.fileId + "." + "mp4")
            }

        else {
                fileType="jpg"
                if (photos != null)
                    photoFile = File(photo.path + File.separator
                            + photos!!.fileId + "." + "jpg")
            }
        if (photoFile.exists()) {
            list= ArrayList()
            list!!.add(photos!!)
            sendMulipleImage(view.context,fileType)
//            val intent = Intent(view.context, UserFragment::class.java)
//            intent.putExtra(ConstantsValues.KEY_FILE_URL, photoFile.absolutePath)
//            view.context.startActivity(intent)
        }
        else
            sendMulipleImage(view.context,fileType)

//        }
    }

    fun sendMulipleImage(context: Context,fileType:String){
        var fileList:ArrayList<File> = ArrayList()
        if (list==null){
            Toast.makeText(context,"Please make a selection to send",Toast.LENGTH_SHORT).show()
            return
        }
        for (i in 0 until list!!.size)
            fileList.add(File(photo.path + File.separator
                    + list!!.get(i)!!.fileId + "." + fileType))
        val intent = Intent(context, UserFragment::class.java)
        intent.putExtra(ConstantsValues.KEY_FILE_LIST, fileList)
        context!!.startActivity(intent)
    }
    fun confirmationDialog(view: View) {
        AlertDialog.Builder(view.context)

                .setMessage("Are you sure you want to delete this file")

                .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, whichButton ->
                    dialog.dismiss()
                    deleteFile(view.context).execute()
                })
                .setNegativeButton(android.R.string.no, null).show()
    }

    fun shareItem(view: View) {
        if (isVideo)
            if (photos!=null)
            photoFile = File(photo.path + File.separator
                    + photos!!.fileId + "." + "mp4")
        else
                if (photos!=null)
            photoFile = File(photo.path + File.separator
                    + photos!!.fileId + "." + "jpg")
//        if (photoFile.exists()) {
//            val sharingIntent = Intent(Intent.ACTION_SEND);
//            sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//            sharingIntent.setType("image/*");
//            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+photoFile.absolutePath));
//            view.context!!.startActivity(Intent.createChooser(sharingIntent, "Share Image Using"));
//        }
//        else
        shareMultipleImage(view.context)
    }

    fun shareMultipleImage(contexts: Context) {
        val intent = Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
        intent.setType("image/*"); /* This example is sharing jpeg images. */

        val files = ArrayList<Uri>();
        if (list==null){
            Toast.makeText(contexts,"please make a selection to share",Toast.LENGTH_SHORT).show()
            return
        }

        for (filesToSend: FileDetails in list!!) {
            if (isVideo)

                photoFile = File(photo.path + File.separator
                        + filesToSend!!.fileId + "." + "mp4")
            else
                photoFile = File(photo.path + File.separator
                        + filesToSend!!.fileId + "." + "jpg")
//            val file = java.io.File(filesToSend.fileId);
            val uri = FileProvider.getUriForFile(contexts, contexts.getApplicationContext().getPackageName() + ".provider", photoFile);
//            val uri = Uri.parse("file://"+photoFile.absolutePath);

            files.add(uri);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        contexts.startActivity(Intent.createChooser(intent, "Share Image Using"));
    }
    inner class deleteFile(val contexts:Context) : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String {
            if (list!!.size>0) {
                for (i in 0 until list!!.size) {
                    val fileId=list!!.get(i)!!.fileId
                    drive!!.files().delete(list!!.get(i)!!.fileId).execute()
                    if (!isVideo)
                        photoFile = File(photo.path + File.separator
                                + list!!.get(i)!!.fileId + "." + "jpg")
                    if (photoFile.exists())
                        photoFile.delete()
                }
            }
            else{
                drive!!.files().delete(photos!!.fileId).execute()
                if (!isVideo)
                    photoFile = File(photo.path + File.separator
                            + photos!!.fileId + "." + "jpg")
                if (photoFile.exists())
                    photoFile.delete()
            }
            return ""
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
//            ProgressBarHandler.hide()
            val intent=Intent()
            intent.putExtra(ConstantsValues.KEY_FILE,photos)
            (contexts as AppCompatActivity).setResult(RESULT_OK,intent)
            (contexts as AppCompatActivity).finish()
//            fileDeletedListener!!.fileDeleted(photos!!)
            Toast.makeText(contexts, "File deleted Successfully", Toast.LENGTH_SHORT).show()

        }

        override fun onPreExecute() {
            super.onPreExecute()
//            ProgressBarHandler.show(contexts!!)

        }
    }

    public interface FileDeletedListener {
        fun fileDeleted(fileId: FileDetails) {

        }
    }
}