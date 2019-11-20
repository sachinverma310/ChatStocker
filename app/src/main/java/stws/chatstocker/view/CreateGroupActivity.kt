package stws.chatstocker.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityCreateGroupBinding
import stws.chatstocker.model.User
import stws.chatstocker.view.adapter.GroupUserAdapter
import stws.chatstocker.view.adapter.UserHorizontalAdapter

import android.net.Uri
import java.io.File
import android.os.Parcelable
import android.content.Intent
import android.R.attr.name
import android.content.ComponentName
import android.content.pm.ResolveInfo
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView

import de.hdodenhof.circleimageview.CircleImageView
import android.graphics.Bitmap
import android.app.Activity
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import stws.chatstocker.ConstantsValues.KEY_LOGIN_DATA
import stws.chatstocker.model.GroupUserList
import stws.chatstocker.utils.GetRealPathUtil
import stws.chatstocker.utils.Prefrences
import stws.chatstocker.viewmodel.GroupUserViewModel

import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class CreateGroupActivity : AppCompatActivity(),GroupUserAdapter.ItemSelectedListener,View.OnClickListener {
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.imgPick->{
                photoFile = getOutputMediaFile()!!
                fileUri = getOutputMediaFileUri(photoFile!!)
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
            R.id.imgCamera->{
                photoFile = getOutputMediaFile()!!
                fileUri = getOutputMediaFileUri(photoFile!!)
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        }
    }

    override fun onItemSelected(position: Int, isRemoved: Boolean) {

    }

    override fun onItemUnSelected(position: Int) {

    }

    private  var adapter:UserHorizontalAdapter?=null
    lateinit var selectedUserList:HashMap<Int,User>
    lateinit var imgPick:ImageView
    lateinit var imgCamera:ImageView
    lateinit var picUri:Uri
    private var photoFile: File? = null
    private var fileUri: Uri? = null
    private lateinit var viewModel:GroupUserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val creteGroupBinding:ActivityCreateGroupBinding=DataBindingUtil.setContentView(this,R.layout.activity_create_group)
        val recyclerView=creteGroupBinding.recyclerView
        recyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        val selectedUser=intent.getSerializableExtra(ConstantsValues.KEY_GRP_USER) as ArrayList<GroupUserList>

        viewModel=ViewModelProviders.of(this).get(GroupUserViewModel::class.java)
        val loginRes=Prefrences.getUserDetails(this,KEY_LOGIN_DATA)
        var myUser:User=User(loginRes.name,loginRes.profile,true,loginRes.email,"",loginRes.uid,false,true)
        selectedUser.add(GroupUserList(0,0,myUser))


        for (i in 0 until selectedUser.size) {
            if (adapter==null) {
                adapter = UserHorizontalAdapter(this, selectedUser.get(i).user!!, this)
                adapter!!.addUser(selectedUser.get(i).user!!)
            }
            else
                adapter!!.addUser(selectedUser.get(i).user!!)
            viewModel.userList.add(selectedUser.get(i).user!!)

        }
        recyclerView.adapter=adapter
        creteGroupBinding.viewModel=viewModel
        imgCamera=creteGroupBinding.imgCamera
        imgPick=creteGroupBinding.imgPick
        imgPick.setOnClickListener(this)
        imgCamera.setOnClickListener(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setTitle(resources.getString(R.string.new_group))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home)
            super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    fun getPickImageChooserIntent(): Intent {

        // Determine Uri of camera image to save.
        val outputFileUri = getOutputMediaFileUri(photoFile!!)

        val allIntents = ArrayList<Intent>()
        val packageManager = packageManager

        // collect all camera intents
        val captureIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            if (outputFileUri != null) {
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            allIntents.add(intent)
        }

        // collect all gallery intents
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        val listGallery = packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            allIntents.add(intent)
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        var mainIntent: Intent = allIntents.get(allIntents.size - 1)
        for (intent in allIntents) {
            if (intent.getComponent()!!.getClassName() == "com.android.documentsui.DocumentsActivity") {
                mainIntent = intent
                break
            }
        }
        allIntents.remove(mainIntent)

        // Create a chooser from the main intent
        val chooserIntent = Intent.createChooser(mainIntent, "Select Group image")

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray())

        return chooserIntent
    }
    /**
     * Get URI to image received from capture by camera.
     */
//    private fun getCaptureImageOutputUri(): Uri? {
//        var outputFileUri: Uri? = null
//        val getImage = externalCacheDir
//        if (getImage != null) {
//            outputFileUri = Uri.fromFile(File(getImage.path, "profile.png"))
//        }
//        return outputFileUri
//    }

    fun getOutputMediaFileUri(file: File): Uri {
        return FileProvider.getUriForFile(this, getPackageName() + ".provider", file)
    }

    private fun getOutputMediaFile(): File? {
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
//        if (type == MEDIA_TYPE_VIDEO) {
//
//            // For unique video file name appending current timeStamp with file name
//            return File(mediaStorageDir.path + File.separator
//                    + "VID_" + timeStamp + "." + "mp4")
//
//        } else {
            return File(mediaStorageDir.path + File.separator
                    + "IMG_" + timeStamp + "." + "jpg")
//        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val bitmap: Bitmap
        if (resultCode == Activity.RESULT_OK) {


            if (data!=null){
//            if (getPickImageResultUri(data!!) != null) {
//                picUri = getPickImageResultUri(data)
                val realPath = GetRealPathUtil.getPath(this, data.data)
                viewModel.grpImageUrl=realPath
                Glide.with(this).load(realPath).into(imgPick)
                try {
//                    myBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, picUri)
//                    myBitmap = rotateImageIfRequired(myBitmap, picUri)
//                    myBitmap = getResizedBitmap(myBitmap, 500)
//
//                    val croppedImageView = findViewById<View>(R.id.img_profile) as CircleImageView
//                    croppedImageView.setImageBitmap(myBitmap)
//                    imageView.setImageBitmap(myBitmap)

                } catch (e: IOException) {
                    e.printStackTrace()
                }


//            }
        }else {
                val realPath: String
                realPath = photoFile!!.getAbsolutePath()
                Log.e("real",realPath)
                Glide.with(this).load(realPath).into(imgPick)
                viewModel.grpImageUrl=realPath
//                val imageUri = data!!.getData() as Uri
//                bitmap = data!!.extras!!.get("data") as Bitmap

//                myBitmap = bitmap
//                val croppedImageView = findViewById<View>(R.id.img_profile) as CircleImageView
//                croppedImageView?.setImageBitmap(myBitmap)

//                imageView.setImageBitmap(myBitmap)

            }

        }

    }

//    fun  getPickImageResultUri( data:Intent):Uri {
//       var  isCamera:Boolean = true;
//        if (data != null) {
//            val action = data.getAction();
//            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
//        }
//
////        if (isCamera)
////            return getCaptureImageOutputUri()!!
//        return data.getData()!!
//    }
}
