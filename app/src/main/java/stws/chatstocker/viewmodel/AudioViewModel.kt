package stws.chatstocker.viewmodel

import android.content.Intent
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.google.api.client.googleapis.media.MediaHttpDownloader
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener
import com.google.api.services.drive.Drive
import stws.chatstocker.ConstantsValues
import stws.chatstocker.view.FullscreenImageActivity
import stws.chatstocker.view.VideoPlayerActivity
import stws.chatstocker.view.adapter.AudioAdapter
import stws.chatstocker.view.adapter.PhotoAdapter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class AudioViewModel(val url: String,val fileId:String, val fileName:String, val drive: Drive, val progressListener: AudioAdapter.FileDownloadProgressListener): ViewModel(), ConstantsValues {
    val photo= File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Chatstocker/Audio")
    val photoFile= File(photo.path+ File.separator
            + fileName)

    var isDownloaded: ObservableField<Boolean>?= ObservableField<Boolean>()
        set(value) {
            field=value
        }
        get() = field



    fun startDownloading(view: View){
        if (!photoFile.exists())
            DownloadTask().execute()
        else{

        }
    }

    inner class DownloadTask: AsyncTask<String, String, String>() , MediaHttpDownloaderProgressListener {
        //            val MEDIA_IN_PROGRESS=101;
//            val MEDIA_IN_PROGRESS_COMPLETED=102;
        override fun progressChanged(downloader: MediaHttpDownloader?) {
            when(downloader!!.getDownloadState()) {
                MediaHttpDownloader.DownloadState.MEDIA_IN_PROGRESS ->
                    publishProgress((downloader.progress * 100).toInt().toString())
                MediaHttpDownloader.DownloadState.MEDIA_COMPLETE -> {

                    publishProgress("100")
                }

            }
        }

        override fun doInBackground(vararg params: String?): String {
            val byteArrayOutputStream =  ByteArrayOutputStream()
            val request= drive.files().get(fileId)
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
            Log.e("progress",values[0])

            progressListener.onProgress(values[0]!!.toFloat())
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            isDownloaded!!.set(true)
        }

    }

}