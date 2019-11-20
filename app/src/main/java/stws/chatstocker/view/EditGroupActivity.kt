package stws.chatstocker.view

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import stws.chatstocker.ConstantsValues
import stws.chatstocker.ConstantsValues.*
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityEditGroupBinding
import stws.chatstocker.model.GroupUserList
import stws.chatstocker.model.User
import stws.chatstocker.utils.GetRealPathUtil
import stws.chatstocker.view.adapter.UserAdapter
import stws.chatstocker.view.adapter.UserHorizontalAdapter
import stws.chatstocker.view.fragments.UserFragment
import stws.chatstocker.viewmodel.EditGroupViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditGroupActivity : AppCompatActivity(),View.OnClickListener {
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
    private var photoFile: File? = null
    private var fileUri: Uri? = null
    lateinit var adapter:UserAdapter
     var groupSize:Int=0
    lateinit var viewModel: EditGroupViewModel
    lateinit var group:User
    lateinit var imgPick: ImageView
    lateinit var imgCamera: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding:ActivityEditGroupBinding=DataBindingUtil.setContentView(this,R.layout.activity_edit_group)
         viewModel=ViewModelProviders.of(this).get(EditGroupViewModel::class.java)
        binding.viewModel=viewModel
        val recyclerView=binding.recyclerView
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.itemAnimator=DefaultItemAnimator()
         group=intent.getParcelableExtra(KEY_GROUP_DETAILS) as User
        Glide.with(this).load(group.image).into(binding.imgPick)
        viewModel.grpName=group.name!!
        viewModel.grpId=group.uid!!
        viewModel.getUserList(group.uid!!,this).observe(this, Observer {
            adapter= UserAdapter(this,it)
            recyclerView.adapter=adapter
            binding.tvParticipant.text=it.size.toString()+" Participent"
            groupSize=it.size
        })
        imgCamera=binding.imgCamera
        imgPick=binding.imgPick
        imgPick.setOnClickListener(this)
        imgCamera.setOnClickListener(this)

//        binding.editGrpActionBar.tvCreatedBy=group.


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
        if (resultCode == Activity.RESULT_OK&&requestCode==200) {


            if (data != null) {
//            if (getPickImageResultUri(data!!) != null) {
//                picUri = getPickImageResultUri(data)
                val realPath = GetRealPathUtil.getPath(this, data.data)
                viewModel.grpImageUrl = realPath
                Glide.with(this).load(realPath).into(imgPick)
                viewModel.sendFile(Uri.fromFile(File(realPath)),this)
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
            } else {
                val realPath: String
                realPath = photoFile!!.getAbsolutePath()
                Log.e("real", realPath)
                Glide.with(this).load(realPath).into(imgPick)
                viewModel.grpImageUrl = realPath
                viewModel.sendFile(Uri.fromFile(File(realPath)),this)
//                val imageUri = data!!.getData() as Uri
//                bitmap = data!!.extras!!.get("data") as Bitmap

//                myBitmap = bitmap
//                val croppedImageView = findViewById<View>(R.id.img_profile) as CircleImageView
//                croppedImageView?.setImageBitmap(myBitmap)

//                imageView.setImageBitmap(myBitmap)

            }
        }
        if (requestCode==ConstantsValues.KEY_EDIT_REQUEST_CODE &&resultCode==Activity.RESULT_OK) {
            val userList: ArrayList<GroupUserList> = data!!.getParcelableArrayListExtra(KEY_GRP_USER)
            viewModel.addUserToGroup(userList, group.uid!!, groupSize).observe(this, Observer {
                startActivity(Intent(this,UserFragment::class.java))
                finish()
            })

            Log.e("hhh", "jhgsdfds")
        }
        }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode==ConstantsValues.KEY_EDIT_REQUEST_CODE) {
//            val userList: ArrayList<GroupUserList> = data!!.getParcelableArrayListExtra(KEY_GRP_USER)
//            viewModel.addUserToGroup(userList, group.uid!!, groupSize).observe(this, Observer {
//                startActivity(Intent(this,UserFragment::class.java))
//                finish()
//            })
//
//            Log.e("hhh", "jhgsdfds")
//        }
//    }
}
