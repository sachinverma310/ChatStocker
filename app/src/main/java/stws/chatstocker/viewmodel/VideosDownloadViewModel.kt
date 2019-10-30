package stws.chatstocker.viewmodel

import android.os.AsyncTask
import android.os.Environment
import androidx.lifecycle.ViewModel
import com.google.api.services.drive.Drive
import stws.chatstocker.utils.DriveServiceHelper
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import java.io.File


class VideosDownloadViewModel(val mDriveService: Drive,val fileId:String):ViewModel() {
    fun downloadVideos(){

    }

}