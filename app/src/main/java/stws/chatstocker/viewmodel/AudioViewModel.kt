package stws.chatstocker.viewmodel

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.google.api.client.googleapis.media.MediaHttpDownloader
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener
import com.google.api.services.drive.Drive
import stws.chatstocker.ConstantsValues
import stws.chatstocker.model.FileDetails
import stws.chatstocker.utils.ProgressBarHandler
import stws.chatstocker.view.FullscreenImageActivity
import stws.chatstocker.view.VideoPlayerActivity
import stws.chatstocker.view.adapter.AudioAdapter
import stws.chatstocker.view.adapter.PhotoAdapter
import stws.chatstocker.view.fragments.UserFragment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class AudioViewModel : ViewModel, ConstantsValues {
    constructor() {

    }

    var url: String = ""
    var fileName: String = ""
    var isVideo: Boolean = false
    var fileId: String = ""
    var drive: Drive? = null
    var progressListener: AudioAdapter.FileDownloadProgressListener? = null
    var pos: Int = 0
    var fileDeletedListener: AudioViewModel.FileDeletedListener? = null
    var photos: FileDetails? = null;

    constructor(url: String, fileId: String, fileName: String, drive: Drive, progressListener: AudioAdapter.FileDownloadProgressListener, pos: Int, fileDeletedListener: FileDeletedListener)
            : this() {
        this.url = url
        this.isVideo = isVideo
        this.fileId = fileId
        this.drive = drive
        this.progressListener = progressListener
        this.pos = pos
        this.fileDeletedListener = fileDeletedListener
        this.photos = photos
        this.fileName = fileName

    }

    val photo = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Chatstocker/Audio")
    var photoFile = File(photo.path + File.separator
            + fileName)

    var isDownloaded: ObservableField<Boolean>? = ObservableField<Boolean>()
        set(value) {
            field = value
        }
        get() = field

    var date: String? = null
        set(value) {
            field = value
        }
        get() = field

    var context: Context? = null
        set(value) {
            field = value
        }
        get() = field
    var list:List<FileDetails>?=null
        set(value) {
            field = value
        }
        get() = field
    fun startDownloading(view: View) {
        if (!photoFile.exists())
            DownloadTask().execute()
        else {

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
        confirmationDialog(view)

    }

    fun shareItem(view: View) {

            photoFile = File(fileName)
        if (photoFile.exists()) {
            val sharingIntent = Intent(Intent.ACTION_SEND);
            sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            sharingIntent.setType("Audio/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(fileName));
            view.context!!.startActivity(Intent.createChooser(sharingIntent, "Share Audio Using"));
//            shareMultipleImage(view.context)
        }
        else
        shareMultipleImage(view.context)
    }

    fun shareMultipleImage(contexts: Context) {
        val intent = Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
        intent.setType("Audio/*"); /* This example is sharing jpeg images. */

        val files = ArrayList<Uri>();

        for (filesToSend: FileDetails in list!!) {
//            if (isVideo)
//                photoFile = File(photo.path + File.separator
//                        + filesToSend!!.fileId + "." + "mp4")
//            else
                photoFile = File(photo.path + File.separator
                        + filesToSend!!.thumbnail )
//            val file = java.io.File(filesToSend.fileId);
            val uri = Uri.parse(photoFile.absolutePath);
            files.add(uri);
        }

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        contexts.startActivity(intent);
    }
    fun sendItem(view: View) {
//        if (isDownloaded!!.get() == true) {

            photoFile = File(photo.path + File.separator
                    + fileName )
        val intent = Intent(view.context, UserFragment::class.java)
        intent.putExtra(ConstantsValues.KEY_FILE_URL, fileName)
        view.context.startActivity(intent)
//        }
    }
    inner class deleteFile : AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String?): String {
            drive!!.files().delete(photos!!.fileId).execute()
            val photoFile = File(photo.path + File.separator
                    + fileId + "." + "3gp")
            if (photoFile.exists())
                photoFile.delete()
            return ""
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val intent=Intent()
            intent.putExtra(ConstantsValues.KEY_FILE,photos)
            (context as AppCompatActivity).setResult(Activity.RESULT_OK,intent)
            (context as AppCompatActivity).finish()
//            fileDeletedListener!!.fileDeleted(photos!!)
            Toast.makeText(context, "File deleted Successfully", Toast.LENGTH_SHORT).show()
//            ProgressBarHandler.hide()
        }

        override fun onPreExecute() {
            super.onPreExecute()
//            ProgressBarHandler.show(context!!)
//            fileDeletedListener!!.fileDeleted(pos)
        }


    }
    fun confirmationDialog(view: View) {
        AlertDialog.Builder(view.context)

                .setMessage("Are you sure you want to delete this file")

                .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, whichButton ->
                    dialog.dismiss()
                    deleteFile().execute()
                })
                .setNegativeButton(android.R.string.no, null).show()
    }

    public interface FileDeletedListener {
        fun fileDeleted(pos: Int)

    }
}