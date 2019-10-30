package stws.chatstocker.view

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import stws.chatstocker.ConstantsValues
import stws.chatstocker.ConstantsValues.KEY_LOGIN_DATA
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityHomeBinding
import stws.chatstocker.view.adapter.HomeAdapter
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.app.ActivityCompat.startActivityForResult
import com.google.android.gms.auth.api.signin.GoogleSignInClient

import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.provider.MediaStore

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


import android.util.Log


import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes

import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide

import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import org.greenrobot.eventbus.Subscribe
import stws.chatstocker.utils.*
import stws.chatstocker.viewmodel.HomeViewModel
import java.io.File
import java.io.IOException
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*


class HomeActivity : BaseActivity(), ConstantsValues, HomeAdapter.OnItemClcik {
    override fun onItemClick(post: Int) {
        Toast.makeText(this, post.toString(), Toast.LENGTH_SHORT).show()
        when (post) {

            0 -> {
                photoFile = getOutputMediaFile(REQUEST_CODE_CAPTURE_IMAGE)!!
                fileUri = getOutputMediaFileUri(photoFile!!)
                openCamera()
            }
            1 -> {
                startActivity(Intent(this,PhotosActivity::class.java))

            }
            2 -> {

            }
            3 -> {
                photoFile = getOutputMediaFile(MEDIA_TYPE_VIDEO)!!
                fileUri = getOutputMediaFileUri(photoFile!!)
                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(intent, MEDIA_TYPE_VIDEO);
            }
            4 -> {
                startActivity(Intent(this,VideoSActivity::class.java))
            }
            5 -> {

            }
            6 -> {
                startActivity(Intent(this, AudioRecordingActivity::class.java))
            }
            7 -> {
                startActivity(Intent(this, AuidosActivity::class.java))
            }
        }

    }

    private lateinit var actvivityHomeBinding: ActivityHomeBinding
    private lateinit var tvName: TextView
    private val TAG = "drive-quickstart"
    private val REQUEST_CODE_SIGN_IN = 0
    private val REQUEST_CODE_CAPTURE_IMAGE = 1
    private val MEDIA_TYPE_VIDEO = 2
    private val MEDIA_TYPE_IMAGE = "image"
    private val REQUEST_CODE_CREATOR = 2
    private var photoFile: File? = null
    private var fileUri: Uri? = null
    lateinit var homeViewModel: HomeViewModel

    lateinit var img: ImageView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actvivityHomeBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_home, frameLayout, true)
        val recyclerView = actvivityHomeBinding.recyclerView as RecyclerView

        recyclerView.layoutManager = GridLayoutManager(this@HomeActivity, 3) as RecyclerView.LayoutManager?
        recyclerView.itemAnimator = DefaultItemAnimator()
        val list = arrayListOf<Drawable>(resources.getDrawable(R.drawable.camera, resources.newTheme()),
                resources.getDrawable(R.drawable.photos, resources.newTheme())
                , resources.getDrawable(R.drawable.photos, resources.newTheme())
                , resources.getDrawable(R.drawable.video_recording, resources.newTheme())
                , resources.getDrawable(R.drawable.video, resources.newTheme())
                , resources.getDrawable(R.drawable.video, resources.newTheme())
                , resources.getDrawable(R.drawable.audio_recording, resources.newTheme())
                , resources.getDrawable(R.drawable.audio, resources.newTheme())
                , resources.getDrawable(R.drawable.audio, resources.newTheme()))
        val homeItemList = resources.getStringArray(R.array.home_items)
        val adapter = HomeAdapter(this, this, list, homeItemList)
        img = actvivityHomeBinding.img
        recyclerView.adapter = adapter
        val intent=intent;
//        signIn()
        if (intent.type!=null) {
        if (intent.data==null)
            for (i in 0 until intent.clipData!!.itemCount) {
                photoFile = File(GetRealPathUtil.getPath(this, intent!!.clipData!!.getItemAt(i).uri));
//            if (data.getClipData() != null) {
//                var count = data.clipData!!.itemCount
//                for (i in 0..count - 1) {
//                    var imageUri: Uri = data!!.clipData!!.getItemAt(i).uri
//                    getPathFromURI(imageUri)
//                }
//            } else if (data.getData() != null) {
//                photoFile = File(data.data!!.path!!)
////                Log.e("imagePath", imagePath);
//            }
//            photoFile = File(URI(imageUri!!.path));
                if (intent.type.equals("image/*"))
                    GetAllFiles(this, "Chat Stocker photos", mDriveServiceHelper, mDriveService, photoFile!!, "image/jpeg").execute()
                else {
                    GetAllFiles(this, "Chat Stocker videos", mDriveServiceHelper, mDriveService, photoFile!!, "video/mp4").execute()
                }
            }

        }
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

//        photoFile = getOutputMediaFile()
//        fileUri = getOutputMediaFileUri(photoFile!!)

    }
    private fun getPathFromURI(uri: Uri) {
        var path: String = uri.path!! // uri = any content Uri

        val databaseUri: Uri
        val selection: String?
        val selectionArgs: Array<String>?
        if (path.contains("/document/image:")) { // files selected from "Documents"
            databaseUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            selection = "_id=?"
            selectionArgs = arrayOf(DocumentsContract.getDocumentId(uri).split(":")[1])
        } else { // files selected from all other sources, especially on Samsung devices
            databaseUri = uri
            selection = null
            selectionArgs = null
        }
        try {
            val projection = arrayOf(
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.ORIENTATION,
                    MediaStore.Images.Media.DATE_TAKEN
            ) // some example data you can query
            val cursor = contentResolver.query(
                    databaseUri,
                    projection, selection, selectionArgs, null
            )
            if (cursor!!.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(projection[0])
                photoFile = File(cursor.getString(columnIndex))
                // Log.e("path", imagePath);
//                imagesPathList.add(imagePath)
            }
            cursor.close()
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
    }

    fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE)
    }

    fun getOutputMediaFileUri(file: File): Uri {
        return FileProvider.getUriForFile(this, getPackageName() + ".provider", file)
    }

    private fun getOutputMediaFile(type: Int): File? {
        val mediaStorageDir = File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Chatstocker")
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        if (type == MEDIA_TYPE_VIDEO) {

            // For unique video file name appending current timeStamp with file name
            return File(mediaStorageDir.path + File.separator
                    + "VID_" + timeStamp + "." + "mp4")

        } else {
            return File(mediaStorageDir.path + File.separator
                    + "IMG_" + timeStamp + "." + "jpg")
        }
    }

    private fun signIn() {
        Log.d(TAG, "Requesting sign-in");

        val signInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(Scope(DriveScopes.DRIVE_FILE))
                        .build();
        val client = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
//        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
//            REQUEST_CODE_SIGN_IN -> {
//                if (resultCode == RESULT_OK) {
//                    Log.i(TAG, "Signed in successfully.");
//                    if (resultCode == Activity.RESULT_OK && data != null) {
//                        handleSignInResult(data);
//                    }
//                }
//            }
            101->{
               val imageUri = data!!.getData() as Uri
                photoFile =  File( URI(imageUri.path));
                GetAllFiles(this,"Chat Stocker photos",mDriveServiceHelper,mDriveService,photoFile!!,"image/jpeg").execute()
            }
            REQUEST_CODE_CAPTURE_IMAGE -> {
                GetAllFiles(this,"Chat Stocker photos",mDriveServiceHelper,mDriveService,photoFile!!,"image/jpeg").execute()
//                GetAllFiles("Chat Stocker photos").execute()


            }
            MEDIA_TYPE_VIDEO -> {
                GetAllFiles(this,"Chat Stocker videos",mDriveServiceHelper,mDriveService,photoFile!!,"video/mp4").execute()
//                GetAllFiles("Chat Stocker videos").execute()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Subscribe
    fun onEvent(intent: Intent) {
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE)
    }

//    inner class GetAllFiles(val folderName: String) : AsyncTask<String, String, List<com.google.api.services.drive.model.File>>() {
//
//        override fun doInBackground(vararg strings: String): List<com.google.api.services.drive.model.File> {
//            val result = ArrayList<com.google.api.services.drive.model.File>()
//            var request: Drive.Files.List? = null
//            try {
//                request = mDriveService.files().list()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//
//            do {
//                try {
//                    val files = request!!.execute()
//
//                    result.addAll(files.files)
//                    request.pageToken = files.nextPageToken
//                } catch (e: IOException) {
//                    println("An error occurred: $e")
//                    request!!.pageToken = null
//                }
//
//            } while (request!!.pageToken != null && request.pageToken.length > 0)
//
//            return result
//        }
//
//        override fun onPostExecute(allFiles: List<com.google.api.services.drive.model.File>) {
//            super.onPostExecute(allFiles)
//            var isExist: Boolean? = false
//            var id: String? = ""
//            for (i in 0 until allFiles.size) {
//                if (allFiles.get(i).getName().equals(folderName)) {
//                    isExist = true
//                    id = allFiles.get(i).id
//                    break
//                }
//
//            }
//            if (isExist!!)
//                mDriveServiceHelper.uploadFile(photoFile, id)
//            else
//                mDriveServiceHelper.createFolder(folderName, photoFile)
//        }
//    }
    fun  getRealPathFromURI(context: Context,  contentUri:Uri):String {
        var cursor: Cursor?= null;
        try {
           val proj = { MediaStore.Images.Media.DATA } as Array<String>
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor?.moveToFirst();
            return cursor!!.getString(column_index!!);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
