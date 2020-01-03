package stws.chatstocker.view


import android.app.Activity
import android.content.*
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.api.services.drive.DriveScopes
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
//import com.iceteck.silicompressorr.SiliCompressor
import com.theartofdev.edmodo.cropper.CropImage
import org.greenrobot.eventbus.Subscribe
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityHomeBinding
import stws.chatstocker.services.FileUploadService
import stws.chatstocker.utils.*
import stws.chatstocker.view.adapter.HomeAdapter
import stws.chatstocker.viewmodel.HomeViewModel
import java.io.*
import java.net.URI
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*


class HomeActivity : BaseActivity(), ConstantsValues, HomeAdapter.OnItemClcik {
    val CROP_PIC = 110
    private var broadcastReceiver: BroadcastReceiver? = null
    override fun onItemClick(post: Int) {
//        Toast.makeText(this, post.toString(), Toast.LENGTH_SHORT).show()
        when (post) {

            0 -> {
                photoFile = getOutputMediaFile(REQUEST_CODE_CAPTURE_IMAGE)!!
                fileUri = getOutputMediaFileUri(photoFile!!)
                openCamera()
            }
            1 -> {
                val intent = Intent(this, PhotosActivity::class.java)
                intent.putExtra(ConstantsValues.KEY_ISFROM_CURRENT, true)
                startActivity(intent)

            }
            2 -> {
                val intent = Intent(this, PhotosActivity::class.java)
                intent.putExtra(ConstantsValues.KEY_ISFROM_CURRENT, false)
                startActivity(intent)
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
                val intent = Intent(this, VideoSActivity::class.java)
                intent.putExtra(ConstantsValues.KEY_ISFROM_CURRENT, true)
                startActivity(intent)

            }
            5 -> {
                val intent = Intent(this, VideoSActivity::class.java)
                intent.putExtra(ConstantsValues.KEY_ISFROM_CURRENT, false)
                startActivity(intent)
            }
            6 -> {

                startActivity(Intent(this, AudioRecordingActivity::class.java))
            }
            7 -> {
                val intent = Intent(this, AuidosActivity::class.java)
                intent.putExtra(ConstantsValues.KEY_ISFROM_CURRENT, true)
                startActivity(intent)
            }
            8 -> {
                val intent = Intent(this, AuidosActivity::class.java)
                intent.putExtra(ConstantsValues.KEY_ISFROM_CURRENT, false)
                startActivity(intent)
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
    lateinit var customIntent: Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actvivityHomeBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_home, frameLayout, true)
        val recyclerView = actvivityHomeBinding.recyclerView as RecyclerView

        recyclerView.layoutManager = GridLayoutManager(this@HomeActivity, 3) as RecyclerView.LayoutManager?
        val spacingInPixels = 10
        recyclerView.addItemDecoration(SpacesItemDecoration(10, SpacesItemDecoration.HORIZONTAL));
        val list = arrayListOf<Drawable>(resources.getDrawable(R.drawable.photos, resources.newTheme()),
                resources.getDrawable(R.drawable.photos_second, resources.newTheme())
                , resources.getDrawable(R.drawable.photos_second, resources.newTheme())
                , resources.getDrawable(R.drawable.video, resources.newTheme())
                , resources.getDrawable(R.drawable.video_second, resources.newTheme())
                , resources.getDrawable(R.drawable.video_second, resources.newTheme())
                , resources.getDrawable(R.drawable.audio, resources.newTheme())
                , resources.getDrawable(R.drawable.audio_second, resources.newTheme())
                , resources.getDrawable(R.drawable.audio_second, resources.newTheme()))
        val homeItemList = resources.getStringArray(R.array.home_items)
        val adapter = HomeAdapter(this, this, list, homeItemList)
        img = actvivityHomeBinding.img
        recyclerView.adapter = adapter

        customIntent = intent;

//        signIn()

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

//        photoFile = getOutputMediaFile()
//        fileUri = getOutputMediaFileUri(photoFile!!)
        getFirebaseToken()

    }

    override fun onStop() {
        super.onStop()
//        if (broadcastReceiver != null) {
//            unregisterReceiver(broadcastReceiver)
//        }

    }

    override fun onDestroy() {
        super.onDestroy()
//        if (broadcastReceiver != null) {
//            unregisterReceiver(broadcastReceiver)
//        }
    }

//    public fun openPath(uri: Uri) {
//        var iss: InputStream? = null;
//        try {
//            val photo = java.io.File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                    "Chatstocker/")
//            val file = File(photo, "1.mp4");
////            FileUtils.cop(inputStream, file);
//            iss = getContentResolver().openInputStream(uri);
//            val buffer = ByteArray(1024); // or other buffer size
//            var read = iss!!.read(buffer!!);
//            val output = BufferedOutputStream(FileOutputStream(file))
//            while (read != -1) {
//                output.write(buffer, 0, read);
//            }
//            //        Uri fileUri = getOutputMediaFileUri(photoFile);
//            try {
//              val  mFilePath = SiliCompressor.with(this).compressVideo(file.absolutePath, "/storage/emulated/0/Pictures/Chatstocker/")
//                Log.e("mfil",mFilePath)
//            } catch (e: URISyntaxException) {
//                e.printStackTrace()
//            }
//            output.flush();
//            //Convert your stream to data here
//            iss!!.close();
//
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace();
//        } catch (e: IOException) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

    private fun registerReceivers() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intents: Intent) {
                if (intents.getBooleanExtra("isLogin", false)) {
                    if (customIntent.type != null) {

                        customIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION and FLAG_GRANT_WRITE_URI_PERMISSION);
                        if (customIntent.data == null)
                            for (i in 0 until customIntent.clipData!!.itemCount) {
                                try {
                                    photoFile = File(RealPathUtil.getRealPath(this@HomeActivity, customIntent!!.clipData!!.getItemAt(i).uri));
//                                    openPath(customIntent!!.clipData!!.getItemAt(i).uri)
//                                    photoFile = File(FileUtils.getPath(this@HomeActivity, customIntent!!.clipData!!.getItemAt(i).uri));
//                    photoFile=  FileProvider.getUriForFile(this, packageName + ".provider", file)
                                } catch (e: Exception) {
                                    Log.e("exc", e.message)
                                }

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
                                if (customIntent.type.equals("image/*"))
                                    GetAllFiles(this@HomeActivity, "Chat Stocker photos", mDriveServiceHelper, mDriveService, photoFile!!, "image/jpeg").execute()
                                else {
                                    GetAllFiles(this@HomeActivity, "Chat Stocker videos", mDriveServiceHelper, mDriveService, photoFile!!, "video/mp4").execute()
                                }
                            }
                        intent=null


                    }
                }
                /*
                 * Step 3: We can update the UI of the activity here
                 * */
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter("stws.chatstocker"))
    }

    private fun getFirebaseToken() {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result!!.token;
                    val userId = Prefrences.getUserUid(this)!!
                    FirebaseDatabase.getInstance().reference.child("User").child(userId).child(ConstantsValues.KEY_DEVICE_TOKEN).setValue(token)


                    // Log and toast
                })
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
        customIntent=intent!!
        if (mDriveServiceHelper!=null){
            customIntent=intent
            if (customIntent.type != null) {

                customIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION and FLAG_GRANT_WRITE_URI_PERMISSION);
                if (customIntent.data == null)
                    for (i in 0 until customIntent.clipData!!.itemCount) {
                        try {
                            photoFile = File(RealPathUtil.getRealPath(this@HomeActivity, customIntent!!.clipData!!.getItemAt(i).uri));
//                                    openPath(customIntent!!.clipData!!.getItemAt(i).uri)
//                                    photoFile = File(FileUtils.getPath(this@HomeActivity, customIntent!!.clipData!!.getItemAt(i).uri));
//                    photoFile=  FileProvider.getUriForFile(this, packageName + ".provider", file)
                        } catch (e: Exception) {
                            Log.e("exc", e.message)
                        }

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
                        if (customIntent.type.equals("image/*") or customIntent.type.equals("image/jpeg"))
                            GetAllFiles(this@HomeActivity, "Chat Stocker photos", mDriveServiceHelper, mDriveService, photoFile!!, "image/jpeg").execute()
                        else {
                            GetAllFiles(this@HomeActivity, "Chat Stocker videos", mDriveServiceHelper, mDriveService, photoFile!!, "video/mp4").execute()
                        }
                    }
            }
        }
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
            101 -> {
                if (resultCode== Activity.RESULT_OK) {
                    val imageUri = data!!.getData() as Uri
                    photoFile = File(URI(imageUri.path));
                    GetAllFiles(this, "Chat Stocker photos", mDriveServiceHelper, mDriveService, photoFile!!, "image/jpeg").execute()

                }
            }
            REQUEST_CODE_CAPTURE_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
//                CropImage.activity(fileUri)
//                        .start(this);
                    GetAllFiles(this, "Chat Stocker photos", mDriveServiceHelper, mDriveService, photoFile!!, "image/jpeg").execute()
//                GetAllFiles("Chat Stocker photos").execute()
                    photoFile = getOutputMediaFile(REQUEST_CODE_CAPTURE_IMAGE)!!
                    fileUri = getOutputMediaFileUri(photoFile!!)
                    openCamera()
                }


            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val result = CropImage.getActivityResult(data);
                    val resultUri = result.getUri();
                    GetAllFiles(this, "Chat Stocker photos", mDriveServiceHelper, mDriveService, File(resultUri.path)!!, "image/jpeg").execute()
                    photoFile = getOutputMediaFile(REQUEST_CODE_CAPTURE_IMAGE)!!
                    fileUri = getOutputMediaFileUri(photoFile!!)
                    openCamera()
                }
            }
            MEDIA_TYPE_VIDEO -> {
                if (resultCode== Activity.RESULT_OK) {
                    GetAllFiles(this, "Chat Stocker videos", mDriveServiceHelper, mDriveService, photoFile!!, "video/mp4").execute()
//                GetAllFiles("Chat Stocker videos").execute()
                }
            }
        }
    }

    private fun performCrop(picUri: Uri) {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            val cropIntent = Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 2);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (anfe: ActivityNotFoundException) {
            Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
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
    fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null;
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

    override fun onResume() {
        super.onResume()


        registerReceivers()
        Log.e("onresume", "on")
    }

    override fun onPause() {
        super.onPause()
        Log.e("onPause", "on")
        unregisterReceiver(broadcastReceiver)
    }


    override fun onStart() {
        super.onStart()
        Log.e("onStart", "on")
    }

    override fun onRestart() {
        super.onRestart()
        Log.e("onRestart", "on")
    }
}
