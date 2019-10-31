package stws.chatstocker.utils

import android.accounts.Account
import android.content.Context
import android.os.AsyncTask
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import java.io.IOException

import kotlin.collections.ArrayList

class GetAllFiles : AsyncTask<String, String, List<File>> {

    var context:Context?=null
    var folderName: String=""
    var mDriveServiceHelper: DriveServiceHelper?=null
    var mDriveService: Drive?=null
    var photoFile:java.io.File?=null
    var  onFileReciveListener: OnFileReciveListener?=null
    var fileType:String?=null

    constructor(context: Context, folderName: String, mDriveServiceHelper: DriveServiceHelper, mDriveService: Drive, photoFile:java.io.File,fileType: String) :
            super() {
        this.context = context
        this.folderName = folderName
        this.mDriveServiceHelper=mDriveServiceHelper
        this.mDriveService=mDriveService
        this.photoFile=photoFile
        this.fileType=fileType
    }
      constructor( context: Context, folderName: String, mDriveServiceHelper: DriveServiceHelper, mDriveService: Drive, onFileReciveListener: OnFileReciveListener,fileType:String):
            super(){
          this.context=context
          this.folderName=folderName
          this.mDriveServiceHelper=mDriveServiceHelper
          this.mDriveService=mDriveService
          this.onFileReciveListener=onFileReciveListener
          this.fileType=fileType

    }


    lateinit var progressBarHandler: ProgressBarHandler
    override fun doInBackground(vararg strings: String): List<com.google.api.services.drive.model.File> {
        val result = ArrayList<File>()
        var request: Drive.Files.List? = null
        try {
            request = mDriveService!!.files().list()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        do {
            try {
                val files = request!!.execute()
                result.addAll(files.files)
                request.pageToken = files.nextPageToken
            } catch (e: IOException) {
                println("An error occurred: $e")
                request!!.pageToken = null
            }

        } while (request!!.pageToken != null && request.pageToken.length > 0)

        return result
    }

    override fun onPostExecute(allFiles: List<com.google.api.services.drive.model.File>) {
        super.onPostExecute(allFiles)
//        ProgressBarHandler.hide()
        var isExist: Boolean? = false
        var id: String? = ""
        for (i in 0 until allFiles.size) {
            if (allFiles.get(i).getName().equals(folderName)) {
                isExist = true
                id = allFiles.get(i).id
                break
            }

        }
//        ProgressBarHandler.hide()
        if(onFileReciveListener!=null){
            onFileReciveListener!!.onFileRecive(id!!)
            return
        }
        if (isExist!!)
            mDriveServiceHelper!!.uploadFile(photoFile, id,fileType)
        else
            mDriveServiceHelper!!.createFolder(folderName, photoFile,fileType)


    }

    override fun onPreExecute() {
        super.onPreExecute()
//        ProgressBarHandler.getInstance()
//        ProgressBarHandler.show(context!!)

    }

    interface OnFileReciveListener{
        fun onFileRecive( id:String)
    }
}