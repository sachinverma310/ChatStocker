package stws.chatstocker.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_profile.*
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityProfileBinding
import stws.chatstocker.model.ChatMessage
import stws.chatstocker.utils.GetRealPathUtil
import stws.chatstocker.utils.Prefrences
import stws.chatstocker.utils.ProgressBarHandler
import stws.chatstocker.viewmodel.ProfileViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {
private lateinit var activityProfileBinding:ActivityProfileBinding
    private lateinit var fabCamera:FloatingActionButton
    private  lateinit var photoFile:File
    private lateinit var fileUri:Uri
    private lateinit var realPath:String
    private lateinit var profilePic:CircleImageView
    private lateinit var mImageStorage: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProfileBinding=DataBindingUtil.setContentView(this,R.layout.activity_profile)
//        setContentView(R.layout.activity_profile)
        val profileViewModel=ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        activityProfileBinding.viewModel=profileViewModel
        val loginResponse=Prefrences.getUserDetails(this, ConstantsValues.KEY_LOGIN_DATA)
        profileViewModel.email=loginResponse.emailOrPhone!!
        profileViewModel.phone=loginResponse.emailOrPhone!!
        profileViewModel.name=loginResponse.name!!
        fabCamera=activityProfileBinding.fabCamera
        profilePic=activityProfileBinding.ivProfile;
        mImageStorage = FirebaseStorage.getInstance().getReference();
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setTitle("Profile")
        Glide.with(this).load(loginResponse.profile).into(profilePic)
        fabCamera.setOnClickListener(View.OnClickListener {
            photoFile = getOutputMediaFile()!!
            fileUri = getOutputMediaFileUri(photoFile)
            val intent=Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivityForResult(intent, 101);
        })

    }

    fun getOutputMediaFileUri(file: File): Uri {
        return FileProvider.getUriForFile(this@ProfileActivity, "$packageName.provider", file)
    }

    private fun getOutputMediaFile(): File? {
        val mediaStorageDir = File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "CameraDemo")
        //        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
        //                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())


        return File(mediaStorageDir.path + File.separator
                + "IMG_" + timeStamp + "." + "jpg")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode==101) {
                realPath = GetRealPathUtil.getPath(this, data!!.getData());
                Glide.with(this).load(realPath).into(ivProfile)
                sendFile(Prefrences.getUserUid(this)!!,Uri.fromFile(File(realPath)))
            }
        }
    }

    private fun sendFile(senderUid: String, path: Uri) {
        var databaseReference = FirebaseDatabase.getInstance().reference
        val progressBarHandler=ProgressBarHandler.getInstance() as ProgressBarHandler
        ProgressBarHandler.show(this)

//        val room_type_1 = senderUid + "_" + receiverUid;
//        val room_type_2 = receiverUid + "_" + senderUid;
        val date = Calendar.getInstance().timeInMillis.toString();
        val user_message_push = databaseReference.child("User")
                .child(senderUid!!).push();
        val push_id = user_message_push.getKey();
        val filePath = mImageStorage.child("profile_images").child(push_id + ".jpg")

        filePath.putFile(path).continueWithTask(object : Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
            override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                if (!task.isSuccessful()) {
                    throw task.getException()!!;
                }
                return filePath.getDownloadUrl();
            }

        }).addOnCompleteListener(object : OnCompleteListener<Uri> {
            override fun onComplete(task: Task<Uri>) {
                if (task.isSuccessful()) {
                    ProgressBarHandler.hide()
                    Toast.makeText(this@ProfileActivity,"Uploaded Sucessfully",Toast.LENGTH_SHORT).show()
                    val downUri = task.getResult();
                    val download_url = downUri.toString()
                    val loginResponse=Prefrences.getUserDetails(this@ProfileActivity,ConstantsValues.KEY_LOGIN_DATA)
                    loginResponse.profile=download_url
                    Prefrences.saveUser(this@ProfileActivity,ConstantsValues.KEY_LOGIN_DATA,loginResponse)
                    Log.d("TAG", "onComplete: Url: " + downUri.toString());
//                    Glide.with(this@ChatActivity).load(Uri.parse(download_url)).into(imgFile)
                   databaseReference.child("User").child(senderUid).child("profileImage").setValue(download_url)

                }
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home)
            super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
