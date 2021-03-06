package stws.chatstocker.view


import android.Manifest
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.devlomi.record_view.RecordButton
import com.devlomi.record_view.RecordView
import com.downloader.PRDownloader
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_bottom_sheets.view.*
import stws.chatstocker.ConstantsValues
import stws.chatstocker.ConstantsValues.*
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityChatBinding
import stws.chatstocker.model.ChatMessage
import stws.chatstocker.model.LoginResponse
import stws.chatstocker.model.User
import stws.chatstocker.utils.GetAllFiles
import stws.chatstocker.utils.GetRealPathUtil
import stws.chatstocker.utils.Prefrences
import stws.chatstocker.utils.Prefrences.Companion.getUserDetails
import stws.chatstocker.view.adapter.ChatAppMsgAdapter
import stws.chatstocker.view.fragments.UserFragment
import stws.chatstocker.viewmodel.ChatMessageViewModel
import zlc.season.rxdownload4.download
import zlc.season.rxdownload4.file
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity(), ConstantsValues, ChatAppMsgAdapter.ItemSelectedListner {
    private var disposable: Disposable? = null
    private lateinit var listAllFile:ArrayList<File>
    private var uploadcount = 0
    private var uploadedListIndex=0;
    private  var isForward=false;
    override fun onItemSelected(selectedList: ArrayList<ChatMessage>?) {

        if (selectedList!!.size > 0) {
            imgMore.visibility = View.GONE
            imgDelete.visibility = View.VISIBLE
            imgSend.visibility = View.VISIBLE
            imgSave.visibility = View.VISIBLE
            if (selectedList!!.size > 1)
                imgSave.visibility = View.GONE
        } else {
            imgMore.visibility = View.VISIBLE
            imgDelete.visibility = View.GONE
            imgSend.visibility = View.GONE
            imgSave.visibility = View.GONE
        }
        imgDelete.setOnClickListener(View.OnClickListener {
           confirmationDialog(imgDelete,selectedList)
        })
        imgSend.setOnClickListener(View.OnClickListener {

            for (i in 0 until selectedList.size) {
                selectedList.get(i).isSelected = false
                selectedList.get(i).from = Prefrences.getUserUid(this)!!
            }
//            if (filelist.size>0) {
            val intent = Intent(this, UserFragment::class.java)
            intent.putParcelableArrayListExtra(KEY_URL_LIST, selectedList!!)
//                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
//            }
        }
        )
        imgSave.setOnClickListener(View.OnClickListener {
            selectedList.get(0).isSelected = false
            adapter.notifyDataSetChanged()
            if (!selectedList.get(0).type.equals("text"))
                saveImageToStocker(selectedList.get(0).msg, selectedList.get(0).type)
            else
                Toast.makeText(this, "You can't save the text message", Toast.LENGTH_SHORT).show()
        }
        )

    }
    fun confirmationDialog(view: View,selectedList: ArrayList<ChatMessage>) {
        AlertDialog.Builder(view.context)

                .setMessage("Are you sure you want to delete this file")

                .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, whichButton ->
                    dialog.dismiss()
                    for (i in 0 until selectedList!!.size) {
                        list.remove(selectedList.get(i))

                    }
                    adapter.notifyDataSetChanged()
                    viewmodel.clearChat(selectedList, myUserId!!, room_type)
                    adapter.selectedMessageList.clear()
                })
                .setNegativeButton(android.R.string.no, null).show()
    }

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
    //    private lateinit var imgFile: ImageView
    private val TAG: String = "firebase"
    private lateinit var mImageStorage: StorageReference
    private lateinit var otherUId: User
    var myUserId: String? = null
    var mLastKey: String = "";
    var mPrevKey: String = "";
    lateinit var list: ArrayList<ChatMessage>
    private var isBlocked: Boolean? = false;
    private var titleMenu: String = "Block User"
    private var room_type: String = ""
    lateinit var viewmodel: ChatMessageViewModel
    lateinit var imgMore: ImageView
    lateinit var imgDelete: ImageView
    lateinit var imgSend: ImageView
    lateinit var recordView: RecordView
    lateinit var recordButton: RecordButton
    lateinit var audioRecordingView: AudioRecordView
    var fileName: String? = null
    private var recorder: MediaRecorder? = null
    var file: File? = null
    var timeStamp: String? = null
    var mediaFiles = ArrayList<String>();
    lateinit var imgAudio: ImageView
    lateinit var imgVideo: ImageView
    lateinit var imggalley: ImageView
    lateinit var imgSave: TextView
//    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatActivityChatBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat)

        val scoresRef = FirebaseDatabase.getInstance().getReference("chat-stocker")
        scoresRef.keepSynced(true)
        PRDownloader.initialize(getApplicationContext());
        imgDelete = chatActivityChatBinding.include.imgDelete
        imgSend = chatActivityChatBinding.include.imgSend
        imgMore = chatActivityChatBinding.include.imgMore
        imgSave = chatActivityChatBinding.include.imgSave

        imggalley = bottom_sheet_chats.imgSend
        imgVideo = bottom_sheet_chats.imgShare
        imgAudio = bottom_sheet_chats.imgDelete
        chatList = java.util.ArrayList()
        otherUId = intent.getParcelableExtra(KEYOTHER_UID)
        audioRecordingView = chatActivityChatBinding.editText
        var loginResponse: LoginResponse? = null
        try {
            loginResponse = getUserDetails(this, KEY_LOGIN_DATA)
            myUserId = loginResponse.uid

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        viewmodel = ViewModelProviders.of(this).get(ChatMessageViewModel::class.java)
        FirebaseDatabase.getInstance().reference.child("Users").child(otherUId.uid!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChild(ConstantsValues.KEY_DEVICE_TOKEN)) {
                    viewmodel.recieverdeviceToken = p0.child(KEY_DEVICE_TOKEN).getValue() as String
                }
            }

        })
        viewmodel.senderUid = myUserId.toString()
        viewmodel.receiverUid = otherUId.uid.toString()
        viewmodel.receiverName = otherUId.name!!
        if (otherUId.online!!)
            viewmodel.lastSeen = "Online"
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
        Collections.synchronizedList(list)
        adapter = ChatAppMsgAdapter(list, this, false)
        mImageStorage = FirebaseStorage.getInstance().getReference();
        recyclerView.adapter = adapter
        viewmodel.getChatResponse().observe(this, Observer<ChatMessage> {
            chatActivityChatBinding.editText.messageView.setText("")
        })
        getAllChat(myUserId, otherUId.uid)
//        imgFile.setOnClickListener(View.OnClickListener {
//            showCameraGalaryPopup()
//            photoFile = getOutputMediaFile()
//            fileUri = getOutputMediaFileUri(photoFile!!)
//        })
        chatActivityChatBinding.include.imgMore.setOnClickListener(View.OnClickListener {
            showPopup(chatActivityChatBinding.include.imgMore)
        })

//         popup = PopupMenu(this, chatActivityChatBinding.include.imgMore);
        Handler().postDelayed(Runnable {
            checkifFilesentFromExternal()
            checkifForwardingUrl()
        }, 1000)

        audioRecordingView.sendView.setOnClickListener(View.OnClickListener {
            viewmodel.message = audioRecordingView.messageView.text.toString()
            isForward=false
            viewmodel.onSendClick(audioRecordingView.messageView)
        })
        audioRecordingView.attachmentView.setOnClickListener(View.OnClickListener {
            bottom_sheet_chats.visibility = View.VISIBLE
//            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
//            showCameraGalaryPopup()
//            photoFile = getOutputMediaFile()
//            fileUri = getOutputMediaFileUri(photoFile!!)
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
//                if (audioRecordingView.timeText.text.length>0)
                startRecording()
                Log.e("rec", "start")
            }

            override fun onRecordingLocked() {
//            startRecording()
                Log.e("rec", "locked")
            }

            override fun onRecordingCompleted() {
//                if (audioRecordingView.timeText.text.length>0)
                stopRecording()
                Log.e("rec", "comp")
            }

            override fun onRecordingCanceled() {
                Log.e("rec", "cancled")
            }

        })

//        sheetBehavior = BottomSheetBehavior.from(bottom_sheet_chat)
////        expandCloseSheet()
//        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                when (newState) {
//                    BottomSheetBehavior.STATE_HIDDEN -> {
//                    }
//                    BottomSheetBehavior.STATE_EXPANDED -> {
//                    }
////                        btBottomSheet.text = "Close Bottom Sheet"
//                    BottomSheetBehavior.STATE_COLLAPSED -> {
//                    }
////                        btBottomSheet.text = "Expand Bottom Sheet"
//                    BottomSheetBehavior.STATE_DRAGGING -> {
//                    }
//                    BottomSheetBehavior.STATE_SETTLING -> {
//                    }
//                }
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//            }
//        })
        imgVideo.setOnClickListener(View.OnClickListener {
            FilePickerBuilder.instance.setMaxCount(5)
                    .setSelectedFiles(mediaFiles)
                    .setActivityTheme(R.style.LibAppTheme)
                    .enableVideoPicker(true)
                    .enableImagePicker(false)
                    .pickPhoto(this);
            bottom_sheet_chats.visibility = View.GONE
        })
        imgAudio.setOnClickListener(View.OnClickListener {
            val fileTypeLis = arrayOf(".mp3", ".wav")

            FilePickerBuilder.instance.setMaxCount(5)
                    .setSelectedFiles(mediaFiles)
                    .setActivityTheme(R.style.LibAppTheme)
                    .enableVideoPicker(false)
                    .enableImagePicker(true)
                    .addFileSupport("ZIP", fileTypeLis, R.drawable.mike)
                    .pickFile(this);
            bottom_sheet_chats.visibility = View.GONE
        })
        imggalley.setOnClickListener(View.OnClickListener {
            FilePickerBuilder.instance.setMaxCount(5)
                    .setSelectedFiles(mediaFiles)
                    .setActivityTheme(R.style.LibAppTheme)
                    .enableVideoPicker(false)
                    .enableImagePicker(true)
                    .pickPhoto(this);
            bottom_sheet_chats.visibility = View.GONE
        })
        recyclerView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                bottom_sheet_chats.visibility = View.GONE
                return false
            }

        })
        editText.setOnClickListener(View.OnClickListener {
            bottom_sheet_chats.visibility = View.GONE
        })
    }


    private fun saveImageToStocker(imagePath: String, fileType: String) {
        val photo = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Chatstocker")
        val fileName = Calendar.getInstance().timeInMillis;
//        val downloadId = PRDownloader.download(imagePath, photoFile!!.absolutePath, fileName.toString())
//                .build()
//                .setOnStartOrResumeListener(OnStartOrResumeListener() {
//
//                })
//                .setOnPauseListener(OnPauseListener() {
//
//                })
//                .setOnCancelListener(OnCancelListener() {
//
//                })
//                .setOnProgressListener(com.downloader.OnProgressListener {
//
//                })
//                .start(object : OnDownloadListener {
//                    override fun onDownloadComplete() {
//                        var photoFile:File?=null
//                        if (fileType.equals("image")) {
//                           photoFile= File(photo.path + File.separator
//                                    + fileName + ".jpg")
//                            GetAllFiles(this@ChatActivity, "Chat Stocker photos", BaseActivity.mDriveServiceHelper, BaseActivity.mDriveService, photoFile!!, "image/jpeg").execute()
//                        }
//                            else if (fileType.equals("video")) {
//                            photoFile= File(photo.path + File.separator
//                                    + fileName + ".mp4")
//                            GetAllFiles(this@ChatActivity, "Chat Stocker videos", BaseActivity.mDriveServiceHelper, BaseActivity.mDriveService, photoFile!!, "video/mp4").execute()
//                        } else if (fileType.equals("video")) {
//                            photoFile= File(photo.path + File.separator
//                                    + fileName + ".mp3")
//                            GetAllFiles(this@ChatActivity, "Chat Stocker audio", BaseActivity.mDriveServiceHelper, BaseActivity.mDriveService, photoFile!!, "audio/mpeg").execute()
//                        } }
//
//                    override fun onError(error: com.downloader.Error?) {
//
//                    }
//
//                });
//        val photo = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                "Chatstocker")
//        val fileName=Calendar.getInstance().timeInMillis;
//        var photoFile = File(photo.path + File.separator
//                + fileName + "." + fileType)
//        Glide.with(this)
//                .asBitmap()
//                .load(imagePath)
//                .into(object : CustomTarget<Bitmap>(){
//                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                        storeImage(resource,photoFile)
//                        GetAllFiles(this@ChatActivity, "Chat Stocker photos", BaseActivity.mDriveServiceHelper, BaseActivity.mDriveService, photoFile!!, "image/jpeg").execute()
//                    }
//                    override fun onLoadCleared(placeholder: Drawable?) {
//                        // this is called when imageView is cleared on lifecycle call or for
//                        // some other reason.
//                        // if you are referencing the bitmap somewhere else too other than this imageView
//                        // clear it here as you can no longer have the bitmap
//                    }
//                })

//        FirebaseDatabase.getInstance().reference.child("message_images")
        disposable = imagePath.download()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = { progress ->
                            //download progress
                            Log.e("progress", "${progress.downloadSizeStr()}/${progress.totalSizeStr()}")
//                            button.text = "${progress.downloadSizeStr()}/${progress.totalSizeStr()}"
//                            button.setProgress(progress)
                        },
                        onComplete = {
                            photoFile = imagePath.file() as File
                            Log.e("filr", photoFile!!.absolutePath)

                            if (fileType.equals("image")) {
//                           photoFile= File(photo.path + File.separator
//                                    + fileName + ".jpg")
                                GetAllFiles(this@ChatActivity, "Chat Stocker photos", BaseActivity.mDriveServiceHelper, BaseActivity.mDriveService, photoFile!!, "image/jpeg").execute()
                            } else if (fileType.equals("video")) {
//                            photoFile= File(photo.path + File.separator
//                                    + fileName + ".mp4")
                                GetAllFiles(this@ChatActivity, "Chat Stocker videos", BaseActivity.mDriveServiceHelper, BaseActivity.mDriveService, photoFile!!, "video/mp4").execute()
                            } else if (fileType.equals("audio")) {
//                            photoFile= File(photo.path + File.separator
//                                    + fileName + ".mp3")
                                GetAllFiles(this@ChatActivity, "Chat Stocker audio", BaseActivity.mDriveServiceHelper, BaseActivity.mDriveService, photoFile!!, "audio/mpeg").execute()
                            }


                            //download complete
//                            button.text = "Open"
                        },
                        onError = {
                            Log.e("failed",it.message)
                            //download failed
//                            button.text = "Retry"
                        }
                )
    }

    private fun storeImage(image: Bitmap, pictureFile: File) {

        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            val fos = FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (e: FileNotFoundException) {
            Log.d(TAG, "File not found: " + e.message);
        } catch (e: IOException) {
            Log.d(TAG, "Error accessing file: " + e.message);
        }
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
                sendFile(Prefrences.Companion.getUserUid(this@ChatActivity), otherUId.uid, Uri.fromFile(from), listFile, 0)
            } catch (e: Exception) {
                Log.e("exception", e.message)
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

    fun checkifFilesentFromExternal() {
        if (intent.getSerializableExtra(KEY_FILE_URL) != null) {
            listAllFile = intent.getSerializableExtra(KEY_FILE_URL) as ArrayList<File>
//            for (i in 0 until listAllFile!!.size)
                sendFile(myUserId, otherUId.uid, null
                        , intent.getSerializableExtra(KEY_FILE_URL) as ArrayList<File>?, 0)
            intent.putExtra(KEY_FILE_URL, "")
        }
    }

    fun checkifForwardingUrl() {

        if (intent.getParcelableArrayListExtra<ChatMessage>(KEY_URL_LIST) != null) {
            val list = intent.getParcelableArrayListExtra<ChatMessage>(KEY_URL_LIST)
            for (i in 0 until list!!.size) {
                isForward=true
                viewmodel.room_type = room_type;
                viewmodel.forwardMessage(list.get(i), this)
            }

//            intent.putExtra(KEY_FILE_URL, "")
        }
    }

    override fun onResume() {
        super.onResume()
//        chatActivityChatBinding.editText.clearFocus();
    }

    fun showPopup(view: ImageView) {
        val popup = PopupMenu(view.context, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.chat_menu, popup.getMenu());
        if (!isBlocked!!)
            popup.menu.getItem(0).title = "Block User"
        else
            popup.menu.getItem(0).title = "Unblock"
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when (item!!.itemId) {
                    R.id.action_block -> {
                        if (!isBlocked!!) {
                            blockUser(myUserId, otherUId.uid)
                            title = "Unblock"

                        } else {
                            unBlockUser(myUserId, otherUId.uid)
                            title = "Block User"
                        }
                    }
                    R.id.action_clear -> {
                        viewmodel.clearChat(list, myUserId!!, room_type)
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
        databaseReference.child("blocked_user").ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.hasChild(room_type_1)) {
                    titleMenu = "Unblock"
                    isBlocked = true;
                    viewmodel.isBlocked = true

                    return
                } else {
                    if (p0.hasChild(room_type_2)) {
                        isBlocked = true;
                        titleMenu = "Block User"
                        viewmodel.isBlocked = true
                        room_type = room_type_2
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
                            viewmodel.room_type = "1"
                            room_type = room_type_1

                            FirebaseDatabase.getInstance().reference.child("chat_room").child(room_type_1).addChildEventListener(object : ChildEventListener {
                                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                                    updateChatLayout(dataSnapshot)
                                }

                                override fun onCancelled(p0: DatabaseError) {
                                    Log.e("str", p0.message)
                                }

                                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                                    Log.e("strp1", p1)
                                }

                                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                                    if (p0.child("sentToserver").getValue() as Boolean) {

                                        for (i in 0 until list.size) {
                                            if (list.get(i).date.equals(p0.key.toString())) {
                                                list.get(i).isSentToserver = p0.child("sentToserver").getValue() as Boolean
                                                adapter.notifyItemChanged(i)
                                                break
                                            }
                                        }


//                                        scrollChatLayouttoBottom()
                                    }
//                                    Log.e("strp2", p0.child("sentToserver").getValue().toString())
//                                    Log.e("strp2", p0.child("seen").getValue().toString())
                                }


                                override fun onChildRemoved(p0: DataSnapshot) {
                                    Log.e("strp4", p0.key)
                                }
                            })
                        } else if (dataSnapshot.hasChild(room_type_2)) {
                            room_type = room_type_2
                            viewmodel.room_type = "2"
                            FirebaseDatabase.getInstance().reference.child("chat_room").child(room_type_2).addChildEventListener(object : ChildEventListener {
                                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                                    updateChatLayout(dataSnapshot)
                                }

                                override fun onCancelled(p0: DatabaseError) {
                                    Log.e("str", p0.message)
                                }

                                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                                    Log.e("strp1", p1)
                                }

                                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                                    if (p0.child("sentToserver").getValue() as Boolean) {
                                        for (i in 0 until list.size) {
                                            if (list.get(i).date.equals(p0.key.toString())) {
                                                list.get(i).isSentToserver = p0.child("sentToserver").getValue() as Boolean
                                                adapter.notifyItemChanged(i)
                                                break
                                            }
                                        }

                                    }
//                                    Log.e("strp2", p0.child("sentToserver").getValue().toString())
//                                    Log.e("strp2", p0.child("seen").getValue().toString())

                                }


                                override fun onChildRemoved(p0: DataSnapshot) {
                                    Log.e("strp4", p0.key)
                                }
                            })
                        } else {
                            room_type = room_type_1
                            viewmodel.room_type = "1"

                            FirebaseDatabase.getInstance().reference.child("chat_room").child(room_type_1).addChildEventListener(object : ChildEventListener {
                                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                                    updateChatLayout(dataSnapshot)
                                }

                                override fun onCancelled(p0: DatabaseError) {
                                    Log.e("str", p0.message)
                                }

                                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                                    Log.e("strp1", p1)
                                }

                                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                                    if (p0.child("sentToserver").getValue() as Boolean) {
                                        for (i in 0 until list.size) {
                                            if (list.get(i).date.equals(p0.key.toString())) {
                                                list.get(i).isSentToserver = p0.child("sentToserver").getValue() as Boolean
                                                adapter.notifyItemChanged(i)
                                                break
                                            }
                                        }

                                    }
//                                    Log.e("strp2", p0.child("sentToserver").getValue().toString())
//                                    Log.e("strp2", p0.child("seen").getValue().toString())
                                }


                                override fun onChildRemoved(p0: DataSnapshot) {
                                    Log.e("strp4", p0.key)
                                }
                            })
                        }


                    }

                })

    }

    private fun updateChatLayout(dataSnapshot: DataSnapshot) {
        if (dataSnapshot.value != null) {
            if (!dataSnapshot.hasChild(myUserId!!)) {

                val chatMessage = dataSnapshot.getValue(ChatMessage::class.java!!)
                chatMessage!!.date = dataSnapshot.key.toString()
                chatMessage.isSent = false
                viewmodel.updatelstChatTime(chatMessage!!.date)
                if (!dataSnapshot.hasChild("now")) {
                    list.add(chatMessage)
                    scrollChatLayouttoBottom()
                    return
                }
//                                            if (list.size > 0) {
//                                                if (!list.get(adapter.itemCount - 1).date.equals(chatMessage.date)) {
//                                                    list.add(chatMessage)
//                                                    scrollChatLayouttoBottom()
//                                                }
//                                            }
                else if (dataSnapshot.hasChild("now")) {
                    val from = dataSnapshot.child("from").getValue();
                    val text = dataSnapshot.child("type").getValue();
                    if (text!!.equals("text")){
                        list.add(chatMessage)
                        scrollChatLayouttoBottom()
                    }
                   else if (!from!!.equals(myUserId)) {
                        list.add(chatMessage)
                        scrollChatLayouttoBottom()
//                        return

                    }
                    else if (isForward){
                        list.add(chatMessage)
                        scrollChatLayouttoBottom()

                    }
//                    else {
//                        list.add(chatMessage)
                        FirebaseDatabase.getInstance().reference.child("chat_room").child(room_type)
                                .child(dataSnapshot.key.toString()).child("now").removeValue()
//                        scrollChatLayouttoBottom()
//                    }

                }
            }
//                                        adapter = ChatAdapter(this@ChatActivity, list)
//                                        adapter.addMessage(chatMessage)


            editText.messageView.setText("")
//                                                recyclerView.scrollToPosition(list.size-1);
        }
    }

    private fun scrollChatLayouttoBottom() {
        val newMsgPosition = list.size - 1;

        // Notify recycler view insert one new data.
        adapter.notifyItemInserted(newMsgPosition);

        // Scroll RecyclerView to the last message.
        recyclerView.scrollToPosition(newMsgPosition);
    }


    private fun sendFile(senderUid: String?, receiverUid: String?, path: Uri?, pathList: ArrayList<File>?, i: Int) {
        isForward=false
        var date: String? = ""

        var filename: String? = ""
        var fileType: String? = ""
        var fileExtension: String? = ""
        var chatMessage: ChatMessage? = null
        val photo = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Chatstocker")



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
        for (i in 0 until pathList.size) {
            date = Calendar.getInstance().timeInMillis.toString();
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
        }
        uploadedListIndex=list.size-pathList.size
        upload(senderUid,receiverUid,pathList,0)




    }

    private fun upload(senderUid: String?, receiverUid: String?,pathList: ArrayList<File>,i:Int){
        var databaseReference = FirebaseDatabase.getInstance().reference
        var fileExtension: String? = ""
        val room_type_1 = senderUid + "_" + receiverUid;
        val room_type_2 = receiverUid + "_" + senderUid;
        var user_message_push: DatabaseReference? = null
        var push_id: String? = ""

        user_message_push = databaseReference.child("chat_room")
                .child(senderUid!!).child(receiverUid!!).push();
        push_id = user_message_push!!.getKey()!!;
        val filePath = mImageStorage.child("message_images").child(push_id + fileExtension)

        filePath.putFile(Uri.fromFile(pathList.get(i))).addOnProgressListener {
            val progress = (100.0 * it.bytesTransferred / it.totalByteCount)
//            index = list.indexOf(chatMessage)
            list.get(uploadedListIndex).progressValue = progress.toInt()
            if (progress.toInt() == 100) {
                list.get(uploadedListIndex).isSent = true

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
                    val date = Calendar.getInstance().timeInMillis.toString();
                    list.get(uploadedListIndex).date=date;
                    val chat = ChatMessage(download_url, "flase", list.get(uploadedListIndex).type!!, senderUid!!, list.get(uploadedListIndex).date!!, receiverUid!!, myUserId!!, false, otherUId.name!!)
                    val messageMap = HashMap<Any, Any>();
                    messageMap.put("message", download_url);
                    messageMap.put("seen", false);
                    messageMap.put("type", list.get(uploadedListIndex).type!!);
                    messageMap.put("time", ServerValue.TIMESTAMP);
                    messageMap.put("from", senderUid!!);

                    val messageUserMap = HashMap<String, Any>();
                    messageUserMap.put(senderUid + "/" + push_id, messageMap);
                    messageUserMap.put(receiverUid + "/" + push_id, messageMap);
//                    adapter.addMessage(chat)
//                    recyclerView.adapter = adapter
                    chat.isNow = "0"

                    if (room_type == "")
                        room_type = senderUid + "_" + receiverUid
                    databaseReference.child("chat_room").child(room_type)
                            .child(date)
                            .setValue(chat)


                    viewmodel.sendNotifcationtoUser(Prefrences.getUserDetails(this@ChatActivity, KEY_LOGIN_DATA).name!!, chat.msg, this@ChatActivity, date, room_type)
                    uploadcount++
                    uploadedListIndex++
                    if (uploadcount<pathList.size)
                        upload(senderUid,receiverUid,pathList,uploadcount)
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
        FilePickerBuilder.instance.setMaxCount(5)
                .setSelectedFiles(mediaFiles)
                .setActivityTheme(R.style.LibAppTheme)
                .enableVideoPicker(true)
                .enableImagePicker(false)
                .pickPhoto(this);
//        val galleryIntent =  Intent(Intent.ACTION_GET_CONTENT);
//    intent.setType("*/*");
//    intent.addCategory(Intent.CATEGORY_OPENABLE)
////        val galleryIntent = Intent(Intent.ACTION_PICK,
////                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(galleryIntent, 200)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {


            if (resultCode == RESULT_OK) {
                var list: ArrayList<File> = ArrayList()
//                var listFile: ArrayList<ImageFile> = ArrayList()
                if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
//                    Log.e("path", photoFile!!.getAbsolutePath())
                    mediaFiles = ArrayList<String>();
                    mediaFiles.addAll(data!!.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
//                    val takenImage = BitmapFactory.decodeFile(photoFile!!.getAbsolutePath())
//                    val realPath: String
//                    realPath = photoFile!!.getAbsolutePath()
//                    list.add(File(realPath))
                    for (i in 0 until mediaFiles.size) {
                        list.add(File(mediaFiles.get(i)))


                    }//                    Glide.with(this@ChatActivity).load(realPath).into(imgFile)
                    sendFile(Prefrences.Companion.getUserUid(this), otherUId.uid, Uri.fromFile(File(mediaFiles.get(0))), list, 0)
                }
                if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
//                    Log.e("path", photoFile!!.getAbsolutePath())
                    mediaFiles = ArrayList<String>();
                    mediaFiles.addAll(data!!.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
//                    val takenImage = BitmapFactory.decodeFile(photoFile!!.getAbsolutePath())
//                    val realPath: String
//                    realPath = photoFile!!.getAbsolutePath()
//                    list.add(File(realPath))
                    for (i in 0 until mediaFiles.size) {
                        list.add(File(mediaFiles.get(i)))


                    }//                    Glide.with(this@ChatActivity).load(realPath).into(imgFile)
                    sendFile(Prefrences.Companion.getUserUid(this), otherUId.uid, Uri.fromFile(File(mediaFiles.get(0))), list, 0)
                } else if (requestCode == 200) {

                    val realPath: String
                    realPath = GetRealPathUtil.getPath(this@ChatActivity, data!!.data)
                    list.add(File(realPath))
//                    Glide.with(this@ChatActivity).load(realPath).into(imgFile)
                    sendFile(Prefrences.Companion.getUserUid(this), otherUId.uid, Uri.fromFile(File(realPath)), list, 0)
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
