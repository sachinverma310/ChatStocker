package stws.chatstocker.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.ceylonlabs.imageviewpopup.ImagePopup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_profile.*
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityProfileBinding
import stws.chatstocker.model.ChatMessage
import stws.chatstocker.model.LoginResponse
import stws.chatstocker.utils.GetRealPathUtil
import stws.chatstocker.utils.Prefrences
import stws.chatstocker.utils.ProgressBarHandler
import stws.chatstocker.viewmodel.ProfileViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ProfileActivity : AppCompatActivity() {
    private lateinit var activityProfileBinding: ActivityProfileBinding
    private lateinit var fabCamera: FloatingActionButton
    private lateinit var photoFile: File
    private lateinit var fileUri: Uri
    private lateinit var realPath: String
    private lateinit var profilePic: CircleImageView
    private lateinit var mImageStorage: StorageReference
    private lateinit var btnUpdate: Button
    var PHONE_NO_REUEST_CODE: Int = 101;
    val CROP_PIC = 105
    private var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var token: String? = null
    private lateinit var mVerificationId: String
    private val TAG: String = "MyTag"
    private var mVerificationInProgress: Boolean = false
    private lateinit var edtMobile: EditText
    private lateinit var loginResponse: LoginResponse;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        // Op
//        setContentView(R.layout.activity_profile)
        val profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        val isfromAct = intent.getBooleanExtra(ConstantsValues.KEY_ISFROM_ACT, false)

        activityProfileBinding.viewModel = profileViewModel
        loginResponse = Prefrences.getUserDetails(this, ConstantsValues.KEY_LOGIN_DATA)
        profileViewModel.email = loginResponse.email!!
        profileViewModel.phone = loginResponse.phone!!
        profileViewModel.name = loginResponse.name!!
        btnUpdate = activityProfileBinding.btnUpdate
        edtMobile = activityProfileBinding.etPhoneNo
        fabCamera = activityProfileBinding.fabCamera
        profilePic = activityProfileBinding.ivProfile;
        mImageStorage = FirebaseStorage.getInstance().getReference();
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setTitle("Profile")
        if (isfromAct) {
            btnUpdate.visibility = View.INVISIBLE
            edtMobile.isFocusableInTouchMode=false
            setTitle("Account Information")
            fabCamera.isClickable=false;
        }
        Glide.with(this).load(loginResponse.profile).into(profilePic)
        fabCamera.setOnClickListener(View.OnClickListener {
            photoFile = getOutputMediaFile()!!
            fileUri = getOutputMediaFileUri(photoFile)
            val intent = Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivityForResult(intent, 101);
        })


        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                ProgressBarHandler.hide()
//                Log.w(TAG, "onVerificationFailed", e)
                if (e is FirebaseAuthInvalidCredentialsException) {
//                    .setError("Invalid phone number.")
                } else if (e is FirebaseTooManyRequestsException) {
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                //                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            override fun onCodeSent(verificationId: String?,
                                    token: PhoneAuthProvider.ForceResendingToken?) {
                ProgressBarHandler.hide()
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId!!)
                Toast.makeText(this@ProfileActivity, "Verification Code sent to your mobile number", Toast.LENGTH_SHORT).show()
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                mResendToken = token
                startActivityForResult(Intent(this@ProfileActivity, OtpverifyActivity::class.java).putExtra(ConstantsValues.KEY_VERIFICATION_ID, verificationId), PHONE_NO_REUEST_CODE)

                // [START_EXCLUDE]
                // Update UI
                //                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        }

        btnUpdate.setOnClickListener(View.OnClickListener {
            if (!edtMobile.text.toString().equals(profileViewModel.phone)) {
                if (edtMobile.length() == 10) {
                    startPhoneNumberVerification("+91" + edtMobile.text.toString())
                }
            }
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
            if (requestCode == 101) {
                realPath = GetRealPathUtil.getPath(this, data!!.getData());
                CropImage.activity(Uri.fromFile(File(realPath)))
                        .start(this);
//                CropImage.activity(Uri.parse(realPath))
//                        .start(this);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data);
                val resultUri = result.getUri();
                Glide.with(this).load(resultUri).into(ivProfile)
                sendFile(Prefrences.getUserUid(this)!!, resultUri)
            }
        }
        if (requestCode == PHONE_NO_REUEST_CODE) {
//            verifyPhoneNumberWithCode(data!!.getStringExtra(ConstantsValues.KEY_VERIFICATION_ID), data.getStringExtra(ConstantsValues.KEY_OTP))
//            if (lastPhone.equals(phone)){
            FirebaseDatabase.getInstance().reference.child("Users").child(Prefrences.getUserUid(this)!!).child("numbers")
                    .setValue(edtMobile.text.toString())
            loginResponse.phone = edtMobile.text.toString()
            Prefrences.saveUser(this, ConstantsValues.KEY_LOGIN_DATA, loginResponse)

        }
    }

    private fun sendFile(senderUid: String, path: Uri) {
        var databaseReference = FirebaseDatabase.getInstance().reference
        val progressBarHandler = ProgressBarHandler.getInstance() as ProgressBarHandler
        ProgressBarHandler.show(this)

//        val room_type_1 = senderUid + "_" + receiverUid;
//        val room_type_2 = receiverUid + "_" + senderUid;
        val date = Calendar.getInstance().timeInMillis.toString();
        val user_message_push = databaseReference.child("Users")
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
                    Toast.makeText(this@ProfileActivity, "Uploaded Sucessfully", Toast.LENGTH_SHORT).show()
                    val downUri = task.getResult();
                    val download_url = downUri.toString()
                    val loginResponse = Prefrences.getUserDetails(this@ProfileActivity, ConstantsValues.KEY_LOGIN_DATA)
                    loginResponse.profile = download_url
                    Prefrences.saveUser(this@ProfileActivity, ConstantsValues.KEY_LOGIN_DATA, loginResponse)
                    Log.d("TAG", "onComplete: Url: " + downUri.toString());
//                    Glide.with(this@ChatActivity).load(Uri.parse(download_url)).into(imgFile)
                    databaseReference.child("Users").child(senderUid).child("profileImage").setValue(download_url)

                }
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }


    private fun startPhoneNumberVerification(phoneNumber: String) {
        ProgressBarHandler.getInstance()
        ProgressBarHandler.show(this@ProfileActivity)
        // [START start_phone_auth]
//
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this@ProfileActivity, // Activity (for callback binding)
                mCallbacks)        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true
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
}
