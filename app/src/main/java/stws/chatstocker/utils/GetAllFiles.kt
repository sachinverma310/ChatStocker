package stws.chatstocker.utils

import android.content.Context
import android.os.AsyncTask
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import java.io.IOException
import java.util.ArrayList

class GetAllFiles(val context: Context,val folderName: String,val mDriveServiceHelper: DriveServiceHelper,val mDriveService: Drive,val photoFile:java.io.File) : AsyncTask<String, String, List<File>>() {
lateinit var progressBarHandler: ProgressBarHandler
    override fun doInBackground(vararg strings: String): List<com.google.api.services.drive.model.File> {
        val result = ArrayList<File>()
        var request: Drive.Files.List? = null
        try {
            request = mDriveService.files().list()
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
        var isExist: Boolean? = false
        var id: String? = ""
        for (i in 0 until allFiles.size) {
            if (allFiles.get(i).getName().equals(folderName)) {
                isExist = true
                id = allFiles.get(i).id
                break
            }

        }
        if (isExist!!)
            mDriveServiceHelper.uploadFile(photoFile, id)
        else
            mDriveServiceHelper.createFolder(folderName, photoFile)
        ProgressBarHandler.hide()
    }

    override fun onPreExecute() {
        super.onPreExecute()
        ProgressBarHandler.getInstance()
        ProgressBarHandler.show(context)

    }
}