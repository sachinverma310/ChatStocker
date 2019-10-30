package stws.chatstocker.view

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_chat.*
import stws.chatstocker.ConstantsValues
import stws.chatstocker.ConstantsValues.KEYOTHER_UID
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityChatBinding
import stws.chatstocker.model.ChatMessage
import stws.chatstocker.model.User
import stws.chatstocker.utils.GetRealPathUtil
import stws.chatstocker.utils.Prefrences
import stws.chatstocker.view.adapter.ChatAdapter
import stws.chatstocker.view.adapter.ChatAppMsgAdapter
import stws.chatstocker.viewmodel.ChatMessageViewModel
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity(), ConstantsValues {
    private lateinit var chatActivityChatBinding: ActivityChatBinding
    private var onceLoaded: Boolean = false
//    lateinit var adapter: ChatAdapter
    lateinit var adapter: ChatAppMsgAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    //    private lateinit var chatPageActionBarBinding: ChatPageActionBarBinding
    private lateinit var chatList: ArrayList<ChatMessage>
    private var photoFile: File? = null
    private var fileUri: Uri? = null
    private lateinit var imageStoragePath: String
    private lateinit var imgFile: ImageView
    private val TAG: String = "firebase"
    private lateinit var mImageStorage: StorageReference
    private lateinit var otherUId: User
     var  myUserId:String?=null
     var  mLastKey:String="";
    var  mPrevKey:String="";
   lateinit var  list : ArrayList<ChatMessage>
    private var isBlocked:Boolean?=false;
    private  var titleMenu: String="Block User"
    private  var room_type:String=""
    lateinit var   viewmodel:ChatMessageViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatActivityChatBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        imgFile = chatActivityChatBinding.imgFileUpload
//        chatActivityChatBinding=ActivityChatBinding.inflate(layoutInflater)

        chatList = java.util.ArrayList()
        otherUId = intent.getParcelableExtra(KEYOTHER_UID)
         myUserId = Prefrences.Companion.getUserUid(this)
         viewmodel = ViewModelProviders.of(this).get(ChatMessageViewModel::class.java)
        viewmodel.senderUid = myUserId.toString()
        viewmodel.receiverUid = otherUId.uid.toString()
        if (otherUId.online!!)
            viewmodel.lastSeen="Online"
        else
        viewmodel.lastSeen = otherUId.lastSeen.toString()
        viewmodel.name = otherUId.name.toString()
        viewmodel.userPic = otherUId.image.toString()
        viewmodel.isloadedOnce = false;
//        viewmodel.userPic=otherUId.toString()
        chatActivityChatBinding.viewModel = viewmodel
//        chatPageActionBarBinding.viewModel=viewmodel
        val recyclerView = chatActivityChatBinding.recyclerView
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

         list = ArrayList<ChatMessage>()
        adapter = ChatAppMsgAdapter( list)
        mImageStorage = FirebaseStorage.getInstance().getReference();
        recyclerView.adapter = adapter
        viewmodel.getChatResponse().observe(this, Observer<ChatMessage> {
        })
        getAllChat(myUserId, otherUId.uid)
        imgFile.setOnClickListener(View.OnClickListener {
            showCameraGalaryPopup()
            photoFile = getOutputMediaFile()
            fileUri = getOutputMediaFileUri(photoFile!!)
        })
        chatActivityChatBinding.include.imgMore.setOnClickListener(View.OnClickListener {
            showPopup(chatActivityChatBinding.include.imgMore)
        })
//         popup = PopupMenu(this, chatActivityChatBinding.include.imgMore);
    }

    fun showPopup(view: ImageView) {
        val popup = PopupMenu(view.context, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.chat_menu, popup.getMenu());
        if (!isBlocked!!)
        popup.menu.getItem(0).title="Block User"
        else
            popup.menu.getItem(0).title="Unblock"
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when (item!!.itemId) {
                    R.id.action_block -> {
                        if (!isBlocked!!) {
                            blockUser(myUserId, otherUId.uid)
                            title="Unblock"

                        }
                        else {
                            unBlockUser(myUserId, otherUId.uid)
                            title="Block User"
                        }
                    }
                    R.id.action_clear->{
                        viewmodel.clearChat(list,myUserId!!,room_type)
                        list.clear()
                        adapter.notifyDataSetChanged()
                    }
                }
                return true
            }

        });

        popup.show();//showing popup menu
    }

    private fun getAllChat(senderUid: String?, receiverUid: String?) {

        val room_type_1 = senderUid + "_" + receiverUid
        val room_type_2 = receiverUid + "_" + senderUid
        val databaseReference = FirebaseDatabase.getInstance()
                .reference
        databaseReference.child("blocked_user").ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChild(room_type_1)) {
                    titleMenu="Unblock"
                    isBlocked=true;
                    viewmodel.isBlocked=true

                    return
                }
                else{
                    if (p0.hasChild(room_type_2)){
                        isBlocked=true;
                        titleMenu="Block User"
                        viewmodel.isBlocked=true
                        room_type=room_type_2
                        return
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
        databaseReference.child("chat_room")
                .ref
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.hasChild(room_type_1)) {

                            room_type=room_type_1
                            FirebaseDatabase.getInstance().reference.child("chat_room").child(room_type_1).addChildEventListener(object : ChildEventListener {
                                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                                    if (dataSnapshot.value != null) {
                                        if (!dataSnapshot.hasChild(myUserId!!)) {

                                            val chatMessage = dataSnapshot.getValue(ChatMessage::class.java!!)
                                            chatMessage!!.date = dataSnapshot.key.toString()
                                            list.add(chatMessage)
                                        }
//                                        adapter = ChatAdapter(this@ChatActivity, list)
//                                        adapter.addMessage(chatMessage)

                                        val newMsgPosition = list.size - 1;

                                        // Notify recycler view insert one new data.
                                        adapter.notifyItemInserted(newMsgPosition);

                                        // Scroll RecyclerView to the last message.
                                        recyclerView.scrollToPosition(newMsgPosition);
                                                editText.setText("")
//                                                recyclerView.scrollToPosition(list.size-1);
                                    }
                                }

                                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

                                }

                                override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                                }

                                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

                                }

                                override fun onCancelled(databaseError: DatabaseError) {

                                }
                            })
                        } else if (dataSnapshot.hasChild(room_type_2)) {
                            room_type=room_type_2
                            FirebaseDatabase.getInstance().reference.child("chat_room").child(room_type_2).addChildEventListener(object : ChildEventListener {
                                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                                    if (dataSnapshot.value != null) {
                                        if (!dataSnapshot.hasChild(myUserId!!)) {
                                        val chatMessage = dataSnapshot.getValue(ChatMessage::class.java!!)
                                        chatMessage!!.date = dataSnapshot.key.toString()
                                            list.add(chatMessage)
                                        }
//                                        adapter = ChatAdapter(this@ChatActivity, list)
//                                        adapter.addMessage(chatMessage)
                                        val newMsgPosition = list.size - 1;

                                        // Notify recycler view insert one new data.
                                        adapter.notifyItemInserted(newMsgPosition);

                                        // Scroll RecyclerView to the last message.
                                        recyclerView.scrollToPosition(newMsgPosition);

                                        editText.setText("")
//                                        recyclerView.scrollToPosition(list.size-1);
//                                        adapter = ChatAdapter(this@ChatActivity, list)
//                                        adapter.addMessage(chatMessage)
//                                        recyclerView.adapter = adapter
//                                        recyclerView.adapter = adapter
                                    }
                                }

                                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

                                }

                                override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                                }

                                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

                                }

                                override fun onCancelled(databaseError: DatabaseError) {

                                }
                            })
                        }
                    }

                })

    }

    private fun sendFile(senderUid: String?, receiverUid: String?, path: Uri) {
        var databaseReference = FirebaseDatabase.getInstance().reference
        val room_type_1 = senderUid + "_" + receiverUid;
        val room_type_2 = receiverUid + "_" + senderUid;
        val date = Calendar.getInstance().timeInMillis.toString();
        val user_message_push = databaseReference.child("chat_room")
                .child(senderUid!!).child(receiverUid!!).push();
        val push_id = user_message_push.getKey();
        val filePath = mImageStorage.child("message_images").child(push_id + ".jpg")

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
                    val downUri = task.getResult();
                    val download_url = downUri.toString()
                    Log.d(TAG, "onComplete: Url: " + downUri.toString());
//                    Glide.with(this@ChatActivity).load(Uri.parse(download_url)).into(imgFile)
                    val chat = ChatMessage(download_url, "flase", "image", senderUid, date,receiverUid,myUserId!!)
                    val messageMap = HashMap<Any, Any>();
                    messageMap.put("message", download_url);
                    messageMap.put("seen", false);
                    messageMap.put("type", "image");
                    messageMap.put("time", ServerValue.TIMESTAMP);
                    messageMap.put("from", senderUid);

                    val messageUserMap = HashMap<String, Any>();
                    messageUserMap.put(senderUid + "/" + push_id, messageMap);
                    messageUserMap.put(receiverUid + "/" + push_id, messageMap);
//                    adapter.addMessage(chat)
                    recyclerView.adapter = adapter
                    databaseReference.child("chat_room")
                            .ref
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.hasChild(room_type_1)) {
                                        databaseReference.child("chat_room").child(senderUid + "_" + receiverUid)
                                                .child(date)
                                                .setValue(chat)
                                    } else {
                                        databaseReference.child("chat_room").child(receiverUid + "_" + senderUid)
                                                .child(date)
                                                .setValue(chat)
                                    }
                                }
                            })

                }
            }
        })

    }

    fun getOutputMediaFileUri(file: File): Uri {
        return FileProvider.getUriForFile(this@ChatActivity, packageName + ".provider", file)
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

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            EventBus.getDefault().post(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 0);
        } else {
            takePicture()
        }
    }

    fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (fileUri != null) {
            imageStoragePath = photoFile!!.getAbsolutePath()
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, 100)


    }


    private fun showCameraGalaryPopup() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_camera_layout)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = 500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = lp

        val tvCamera = dialog.findViewById(R.id.tvCamera) as TextView
        val tvGallery = dialog.findViewById(R.id.tvGallery) as TextView
        val tvCancel = dialog.findViewById(R.id.tvCancel) as TextView
        tvCamera.setOnClickListener(View.OnClickListener {
            //                openCamera(view);
            checkPermission()
            dialog.dismiss()
        })
        tvGallery.setOnClickListener(View.OnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                EventBus.getDefault().post(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            } else {
                choosePhotoFromGallary()
            }
            dialog.dismiss()
        })
        tvCancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        dialog.show()
    }


    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, 200)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {


            if (resultCode == RESULT_OK) {

                if (requestCode == 100) {
                    Log.e("path", photoFile!!.getAbsolutePath())
                    val takenImage = BitmapFactory.decodeFile(photoFile!!.getAbsolutePath())
                    val realPath: String
                    realPath = photoFile!!.getAbsolutePath()
                    sendFile(Prefrences.Companion.getUserUid(this), otherUId.uid, Uri.fromFile(File(realPath)))
//                    Glide.with(this@ChatActivity).load(realPath).into(imgFile)

                } else if (requestCode == 200) {
                    val realPath: String
                    realPath = GetRealPathUtil.getPath(this@ChatActivity, data!!.data)
//                    Glide.with(this@ChatActivity).load(realPath).into(imgFile)
                    sendFile(Prefrences.Companion.getUserUid(this), otherUId.uid, Uri.fromFile(File(realPath)))
                    Log.e("realPath", realPath)


                }

            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //                tvCamera.setEnabled(true);
                takePicture()

            }
        } else if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhotoFromGallary()
            }
        }
    }

    private fun blockUser(senderUid: String?, receiverUid: String?) {
        val room_type_1 = senderUid + "_" + receiverUid
        val room_type_2 = receiverUid + "_" + senderUid
        val databaseReference = FirebaseDatabase.getInstance()
                .reference
        databaseReference.child("blocked_user")
                                    .child(room_type_1)
                                    .child("isBlocked")
                                    .setValue(true)
    }
    private fun unBlockUser(senderUid: String?, receiverUid: String?) {
        val room_type_1 = senderUid + "_" + receiverUid

        val databaseReference = FirebaseDatabase.getInstance()
                .reference
        databaseReference.child("blocked_user").removeValue()

    }

    override fun onDestroy() {
        viewmodel.unregisterEventListener()
        super.onDestroy()
    }
}
