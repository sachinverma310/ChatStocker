package stws.chatstocker.view

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaRecorder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_chat.*
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityGroupChatActivtiyBinding
import stws.chatstocker.model.ChatMessage
import stws.chatstocker.model.User
import stws.chatstocker.utils.GetRealPathUtil
import stws.chatstocker.utils.Prefrences
import stws.chatstocker.view.adapter.ChatAppMsgAdapter
import stws.chatstocker.view.fragments.UserFragment
import stws.chatstocker.viewmodel.ChatMessageViewModel
import stws.chatstocker.viewmodel.GroupChatMessgeViewModel
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GroupChatActivtiy : AppCompatActivity(),ChatAppMsgAdapter.ItemSelectedListner {
    override fun onItemSelected(selectedList: ArrayList<ChatMessage>?) {
        if (selectedList!!.size > 0) {
            imgMore.visibility = View.GONE
            imgDelete.visibility = View.VISIBLE
            imgSend.visibility=View.VISIBLE
        } else {
            imgMore.visibility = View.VISIBLE
            imgDelete.visibility = View.GONE
            imgSend.visibility=View.GONE
        }
        imgDelete.setOnClickListener(View.OnClickListener {
            for (i in 0 until selectedList.size) {
                list.remove(selectedList.get(i))
                adapter.notifyDataSetChanged()
            }
            viewmodel.clearChat(selectedList, myUserId!!)
            adapter.selectedMessageList.clear()
        })
        imgSend.setOnClickListener(View.OnClickListener {

            for (i in 0 until selectedList.size) {
                selectedList.get(i).isSelected=false
            }
//            if (filelist.size>0) {
            val intent = Intent(this, UserFragment::class.java)
            intent.putParcelableArrayListExtra(ConstantsValues.KEY_URL_LIST, selectedList!!)
//                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
//            }
        }
        )
    }

    private var photoFile: File? = null
    private var fileUri: Uri? = null
    private lateinit var imageStoragePath: String
    lateinit var imgMore: ImageView
    lateinit var imgDelete: ImageView
    lateinit var imgSend: ImageView
//    private lateinit var imgFile: ImageView
    private val TAG: String = "firebase"
    private lateinit var myUserId:String
    private lateinit var otherUId:User
    private lateinit var viewmodel:GroupChatMessgeViewModel
    lateinit var adapter: ChatAppMsgAdapter
    lateinit var list: ArrayList<ChatMessage>
    private lateinit var chatList: ArrayList<ChatMessage>
    private lateinit var mImageStorage:StorageReference
    private var recorder: MediaRecorder? = null
    var file: File? = null
    var timeStamp: String? = null
    lateinit var audioRecordingView: AudioRecordView
    var fileName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding:ActivityGroupChatActivtiyBinding=DataBindingUtil.setContentView(this,R.layout.activity_group_chat_activtiy)
        myUserId = Prefrences.Companion.getUserUid(this)!!
        viewmodel = ViewModelProviders.of(this).get(GroupChatMessgeViewModel::class.java)
        chatList = java.util.ArrayList()
        otherUId = intent.getParcelableExtra(ConstantsValues.KEYOTHER_UID)
        mImageStorage = FirebaseStorage.getInstance().getReference();
        imgDelete = binding.include.imgDelete
        imgSend= binding.include.imgSend
        imgMore = binding.include.imgMore
//        imgFile=binding.imgFileUpload
        audioRecordingView = binding.editText
        viewmodel.senderUid=myUserId
        viewmodel.groupUid=otherUId.uid!!
        viewmodel.name= Prefrences.getUserDetails(this,ConstantsValues.KEY_LOGIN_DATA).name!!
        viewmodel.groupName=otherUId.name!!
        binding.viewModel=viewmodel
        val recyclerView = binding.recyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        binding.include.imgBack.setOnClickListener(View.OnClickListener {
            super.onBackPressed()
        })
        list = ArrayList<ChatMessage>()
        adapter = ChatAppMsgAdapter(list, this,true)
        mImageStorage = FirebaseStorage.getInstance().getReference();
        recyclerView.adapter = adapter
        viewmodel.getChatResponse().observe(this, Observer<ChatMessage> {
        })
        getAllChat(myUserId, otherUId.uid)
//        imgFile.setOnClickListener(View.OnClickListener {
//            showCameraGalaryPopup()
//            photoFile = getOutputMediaFile()
//            fileUri = getOutputMediaFileUri(photoFile!!)
//        })
        binding.include.imgMore.setOnClickListener(View.OnClickListener {
            showPopup(binding.include.imgMore)
        })
        binding.include.userActionBar.setOnClickListener(View.OnClickListener {
            val intent=Intent(this,EditGroupActivity::class.java)
            intent.putExtra(ConstantsValues.KEY_GROUP_DETAILS,otherUId)
            startActivity(intent)
        })
        Handler().postDelayed(Runnable {
            checkifFilesentFromExternal()
            checkifForwardingUrl()
        }, 1000)
        audioRecordingView.sendView.setOnClickListener(View.OnClickListener {
            viewmodel.message = audioRecordingView.messageView.text.toString()
            viewmodel.onSendClick(audioRecordingView.messageView)
        })
        audioRecordingView.attachmentView.setOnClickListener(View.OnClickListener {
            showCameraGalaryPopup()
            photoFile = getOutputMediaFile()
            fileUri = getOutputMediaFileUri(photoFile!!)
        })
        audioRecordingView.setRecordingListener(object : AudioRecordView.RecordingListener {
            override fun onRecordingStarted() {
                file = File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "/Chatstocker/Audio/")
                timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                if (!file!!.mkdirs()) {
                    file!!.mkdir()
                }
                fileName = "$file" + File.separator + "Audio_" + timeStamp + "." + "mp3"
                startRecording()
            }

            override fun onRecordingLocked() {

            }

            override fun onRecordingCompleted() {
                stopRecording()
            }

            override fun onRecordingCanceled() {

            }

        })
    }

    private fun stopRecording() {
        recorder?.apply {
            try {
                stop()
                release()
                val from = File(file, "Audio_" + timeStamp + ".mp3")
//            var to: File? = null
//            if (userInput.length() > 0)
//                to = File(file, userInput.text.toString() + ".mp3")
//            else
//                to = File(fileName)
//            if (from.exists())
//                from.renameTo(to);
//            GetAllFiles(this@ChatActivity, "Chat Stocker audio", BaseActivity.mDriveServiceHelper, BaseActivity.mDriveService, from, "audio/mpeg").execute()
//            openeRenameDialog()
                var listFile: ArrayList<File> = ArrayList()
                listFile.add(from)
                sendFile(Prefrences.Companion.getUserUid(this@GroupChatActivtiy), otherUId.uid, Uri.fromFile(from), listFile, 0)
            }
            catch (e: Exception){
                Log.e("exception",e.message)
            }
        }

        recorder = null
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("LOG_TAG", "prepare() failed")
            }

            start()
        }
    }

    fun checkifFilesentFromExternal(){
        if (intent.getSerializableExtra(ConstantsValues.KEY_FILE_URL) != null) {
            val list = intent.getSerializableExtra(ConstantsValues.KEY_FILE_URL) as ArrayList<File>?
            for (i in 0 until list!!.size)
                sendFile(myUserId, otherUId.uid, null
                        , intent.getSerializableExtra(ConstantsValues.KEY_FILE_URL) as ArrayList<File>?, i)
            intent.putExtra(ConstantsValues.KEY_FILE_URL,"")
        }
//        if (intent.getStringExtra(ConstantsValues.KEY_FILE_URL)!="")
//            sendFile(myUserId,otherUId.uid,Uri.fromFile(File(intent.getStringExtra(ConstantsValues.KEY_FILE_URL))))
    }
    fun checkifForwardingUrl() {
        if (intent.getParcelableArrayListExtra<ChatMessage>(ConstantsValues.KEY_URL_LIST) != null) {
            val list = intent.getParcelableArrayListExtra<ChatMessage>(ConstantsValues.KEY_URL_LIST)
            for (i in 0 until list!!.size) {
//                viewmodel.room_type=room_type;
                viewmodel.forwardMessage(list.get(i),this)
            }
//            intent.putExtra(KEY_FILE_URL, "")
        }
    }
    fun showPopup(view: ImageView) {
        val popup = PopupMenu(view.context, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.chat_menu, popup.getMenu());
//        if (!isBlocked!!)
            popup.menu.getItem(0).title = "Exit group"
//        else
//            popup.menu.getItem(0).title = "Unblock"
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when (item!!.itemId) {
                    R.id.action_block -> {
                        viewmodel.exitGroup()
                    }
                    R.id.action_clear -> {
                        viewmodel.clearChat(list, myUserId!!)
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

        val room_type_1 =otherUId.uid!!
        val room_type_2 = receiverUid + "_" + senderUid
        val databaseReference = FirebaseDatabase.getInstance()
                .reference
        databaseReference.child("blocked_user").ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
//                if (p0.hasChild(room_type_1)) {
//                    titleMenu = "Unblock"
//                    isBlocked = true;
//                    viewmodel.isBlocked = true
//
//                    return
//                } else {
//                    if (p0.hasChild(room_type_2)) {
//                        isBlocked = true;
//                        titleMenu = "Block User"
//                        viewmodel.isBlocked = true
//                        room_type = room_type_2
//                        return
//                    }
//                }
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
                            FirebaseDatabase.getInstance().reference.child("chat_room").child(room_type_1).addChildEventListener(object : ChildEventListener {
                                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                                    if (dataSnapshot.value != null) {
                                        if (!dataSnapshot.hasChild(myUserId!!)) {

                                            val chatMessage = dataSnapshot.getValue(ChatMessage::class.java!!)
                                            chatMessage!!.date = dataSnapshot.key.toString()
                                            chatMessage.isSent = true
//                                            list.add(chatMessage)

//                                        adapter = ChatAdapter(this@ChatActivity, list)
//                                        adapter.addMessage(chatMessage)
                                        if (!dataSnapshot.hasChild("now")) {
                                            list.add(chatMessage)
                                            scrollChatLayouttoBottom()
                                        }
//                                            if (list.size > 0) {
//                                                if (!list.get(adapter.itemCount - 1).date.equals(chatMessage.date)) {
//                                                    list.add(chatMessage)
//                                                    scrollChatLayouttoBottom()
//                                                }
//                                            }
                                        else if (dataSnapshot.hasChild("now")) {
                                            val isNow = dataSnapshot.child("now").getValue();
                                            if (!isNow!!.equals(dataSnapshot.key.toString())) {
                                                list.add(chatMessage)
                                                scrollChatLayouttoBottom()

                                            }
                                            databaseReference.child("chat_room").child(otherUId.uid!!)
                                                    .child(dataSnapshot.key.toString()).child("now").removeValue()
                                        }
                                    }
//                                        val newMsgPosition = list.size - 1;
//
//                                        // Notify recycler view insert one new data.
//                                        adapter.notifyItemInserted(newMsgPosition);
//
//                                        // Scroll RecyclerView to the last message.
//                                        recyclerView.scrollToPosition(newMsgPosition);
                                        editText.messageView.setText("")
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
                        }

                    }

                })

    }
    private fun scrollChatLayouttoBottom() {
        val newMsgPosition = list.size - 1;

        // Notify recycler view insert one new data.
        adapter.notifyItemInserted(newMsgPosition);

        // Scroll RecyclerView to the last message.
        recyclerView.scrollToPosition(newMsgPosition);
    }
//    private fun sendFile(senderUid: String?, receiverUid: String?, path: Uri) {
//        var databaseReference = FirebaseDatabase.getInstance().reference
//        val room_type_1 = otherUId.uid;
//
//        val date = Calendar.getInstance().timeInMillis.toString();
//        val user_message_push = databaseReference.child("chat_room")
//                .child(senderUid!!).child(receiverUid!!).push();
//        val push_id = user_message_push.getKey();
//        val filename=path.path!!.substring(path.path!!.lastIndexOf("/")+1);
//        var fileType="image";
//        var fileExtension=".jpg"
//        if (filename.contains(".mp4")){
//            fileType="video"
//            fileExtension=".mp4"
//        }
//        else if (filename.contains(".3gp")){
//            fileType="audio"
//            fileExtension=".3gp"
//        }
//        else if (filename.contains(".mp3")){
//            fileType="audio"
//            fileExtension=".mp3"
//        }
//        val filePath = mImageStorage.child("message_images").child(push_id + fileExtension)
//
//        filePath.putFile(path).continueWithTask(object : Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
//            override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {
//                if (!task.isSuccessful()) {
//                    throw task.getException()!!;
//                }
//                return filePath.getDownloadUrl();
//            }
//
//        }).addOnCompleteListener(object : OnCompleteListener<Uri> {
//            override fun onComplete(task: Task<Uri>) {
//                if (task.isSuccessful()) {
//                    val downUri = task.getResult();
//                    val download_url = downUri.toString()
//                    Log.d(TAG, "onComplete: Url: " + downUri.toString());
////                    Glide.with(this@ChatActivity).load(Uri.parse(download_url)).into(imgFile)
//                    val chat = ChatMessage(download_url, "flase", fileType, senderUid, date, receiverUid, myUserId!!, false,otherUId.name!!)
//                    val messageMap = HashMap<Any, Any>();
//                    messageMap.put("message", download_url);
//                    messageMap.put("seen", false);
//                    messageMap.put("type", fileType);
//                    messageMap.put("time", ServerValue.TIMESTAMP);
//                    messageMap.put("from", senderUid);
//
//                    val messageUserMap = HashMap<String, Any>();
//                    messageUserMap.put(senderUid + "/" + push_id, messageMap);
//                    messageUserMap.put(receiverUid + "/" + push_id, messageMap);
////                    adapter.addMessage(chat)
//                    recyclerView.adapter = adapter
//                    databaseReference.child("chat_room")
//                            .ref
//                            .addListenerForSingleValueEvent(object : ValueEventListener {
//                                override fun onCancelled(p0: DatabaseError) {
//
//                                }
//
//                                override fun onDataChange(dataSnapshot: DataSnapshot) {
//
//                                        databaseReference.child("chat_room").child(otherUId.uid!!)
//                                                .child(date)
//                                                .setValue(chat)
//
//                                }
//                            })
//
//                }
//            }
//        })
//
//    }
private fun sendFile(senderUid: String?, receiverUid: String?, path: Uri?, pathList: ArrayList<File>?, i: Int) {
    var databaseReference = FirebaseDatabase.getInstance().reference
    val room_type_1 = senderUid + "_" + receiverUid;
    val room_type_2 = receiverUid + "_" + senderUid;
    var date: String? = ""
    var user_message_push: DatabaseReference? = null
    var push_id: String? = ""
    var filename: String? = ""
    var fileType: String? = ""
    var fileExtension: String? = ""
    var chatMessage: ChatMessage? = null
    val photo = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Chatstocker")
    date = Calendar.getInstance().timeInMillis.toString();


    filename = pathList!!.get(i).path!!.substring(pathList.get(i).path!!.lastIndexOf("/") + 1);
    fileType = "image";
    fileExtension = ".jpg"
    if (filename.contains(".mp4")) {
        fileType = "video"
        fileExtension = ".mp4"
    } else if (filename.contains(".mp3")) {
        fileType = "audio"
        fileExtension = ".mp3"
    }
    chatMessage = ChatMessage(pathList.get(i).path, "", fileType, senderUid!!, "", receiverUid!!, "", false, "")
    chatMessage!!.date = date
    chatMessage.isSent = false
    chatMessage.progressValue = 0
    chatMessage.isNow = date
    list.add(chatMessage)
    if (list.size > 1) {
        val newMsgPosition = list.size - 1;
//
//        // Notify recycler view insert one new data.
        adapter.notifyItemInserted(newMsgPosition);
        recyclerView.scrollToPosition(newMsgPosition);
    } else
        adapter.notifyDataSetChanged()
    var count = 0
    user_message_push = databaseReference.child("chat_room")
            .child(senderUid!!).child(receiverUid!!).push();
    push_id = user_message_push!!.getKey()!!;
    val filePath = mImageStorage.child("message_images").child(push_id + fileExtension)
    var index: Int = 0;
    filePath.putFile(Uri.fromFile(pathList.get(i))).addOnProgressListener {
        val progress = (100.0 * it.bytesTransferred / it.totalByteCount)
        index = list.indexOf(chatMessage)
        list.get(index).progressValue = progress.toInt()
        if (progress.toInt() == 100) {
            list.get(index).isSent = true

        }
        adapter.notifyDataSetChanged()
        Log.e("total", progress.toString());
    }.continueWithTask(object : Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
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
                val chat = ChatMessage(download_url, "flase", fileType!!, senderUid!!, list.get(index).date!!, receiverUid!!, myUserId!!, false, otherUId.name!!)
                val messageMap = HashMap<Any, Any>();
                messageMap.put("message", download_url);
                messageMap.put("seen", false);
                messageMap.put("type", fileType!!);
                messageMap.put("time", ServerValue.TIMESTAMP);
                messageMap.put("from", senderUid!!);

                val messageUserMap = HashMap<String, Any>();
                messageUserMap.put(senderUid + "/" + push_id, messageMap);
                messageUserMap.put(receiverUid + "/" + push_id, messageMap);
//                    adapter.addMessage(chat)
                recyclerView.adapter = adapter
                chat.isNow = list.get(index).date

                databaseReference.child("chat_room").child(otherUId.uid!!)
                        .child(list.get(index).date)
                        .setValue(chat)



                count++
                /*  databaseReference.child("chat_room")
                          .ref
                          .addValueEventListener(object : ValueEventListener {
                              override fun onCancelled(p0: DatabaseError) {

                              }

                              override fun onDataChange(dataSnapshot: DataSnapshot) {
//                                        if (dataSnapshot.hasChild(room_type_1)) {
                                      databaseReference.child("chat_room").child(room_type)
                                              .child(list.get(index).date)
                                              .setValue(chat)
                                      count++
//                                        } else {
//                                            databaseReference.child("chat_room").child(receiverUid + "_" + senderUid)
//                                                    .child(list.get(index).date)
//                                                    .setValue(chat)
//                                            count++
//                                        }
                              }
                          })
*/
            }
        }
    })


}
    fun getOutputMediaFileUri(file: File): Uri {
        return FileProvider.getUriForFile(this, packageName + ".provider", file)
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
                var list: ArrayList<File> = ArrayList()
                if (requestCode == 100) {
                    Log.e("path", photoFile!!.getAbsolutePath())
                    val takenImage = BitmapFactory.decodeFile(photoFile!!.getAbsolutePath())
                    val realPath: String
                    realPath = photoFile!!.getAbsolutePath()
                    list.add(File(realPath))
                    sendFile(Prefrences.Companion.getUserUid(this), otherUId.uid, Uri.fromFile(File(realPath)), list, 0)
//                    sendFile(Prefrences.Companion.getUserUid(this), otherUId.uid, Uri.fromFile(File(realPath)))
//                    Glide.with(this@ChatActivity).load(realPath).into(imgFile)

                } else if (requestCode == 200) {
                    val realPath: String
                    realPath = GetRealPathUtil.getPath(this, data!!.data)
                    list.add(File(realPath))
//                    Glide.with(this@ChatActivity).load(realPath).into(imgFile)
                    sendFile(Prefrences.Companion.getUserUid(this), otherUId.uid, Uri.fromFile(File(realPath)), list, 0)
//                    sendFile(Prefrences.Companion.getUserUid(this), otherUId.uid, Uri.fromFile(File(realPath)))
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
}
