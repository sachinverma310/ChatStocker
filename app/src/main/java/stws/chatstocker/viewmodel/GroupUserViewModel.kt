package stws.chatstocker.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_chat.*
import stws.chatstocker.ConstantsValues
import stws.chatstocker.ConstantsValues.*
import stws.chatstocker.model.ChatMessage
import stws.chatstocker.model.User
import stws.chatstocker.utils.Prefrences
import stws.chatstocker.utils.ProgressBarHandler
import stws.chatstocker.view.ChatActivity
import stws.chatstocker.view.ChatUserActivity
import stws.chatstocker.view.fragments.UserFragment
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GroupUserViewModel : ViewModel, ConstantsValues {
    constructor() {

    }

    constructor(user: User) : this() {
        this.user = user

        this.email = user!!.email
        this.phone = user!!.email
        this.name = user!!.name
        this.lastSeen = user!!.lastSeen
        this.onlineStatus = user!!.online.toString()
        this.uid = user!!.uid.toString();
        this.imgUrl = user!!.image.toString()
    }

    var user: User? = null
    //        get() = field
//        set(value) {
//            field = value
//        }
    var email: String? = ""
    var name: String? = ""
    var phone: String? = ""
    var lastSeen: String? = ""
    var uid: String = ""
    var imgUrl: String = ""
    var grpName: String = ""
    var userList: ArrayList<User> = ArrayList()
    var grpImageUrl: String = ""

    //    set(value) {
//        field=value
//    }
//    get()=field


    var onlineStatus: String? = ""

//    init {
//        this.user = user
//
//        this.email = user!!.email
//        this.phone = user!!.email
//        this.name = user!!.name
//        this.lastSeen = user!!.lastSeen
//        this.onlineStatus = user!!.online.toString()
//        this.uid= user!!.uid.toString();
//        this.imgUrl=user!!.image.toString()
//    }


    fun createGroup(view: View) {
        if (grpName.length == 0)
            return
        else if (grpImageUrl == "") {
            Toast.makeText(view.context, "Select Group Image", Toast.LENGTH_SHORT).show()
            return
        } else if (userList.size == 0) {
            Toast.makeText(view.context, "Select Atleast on user", Toast.LENGTH_SHORT).show()
            return
        }
        var myUserId = Prefrences.getUserUid(view.context)

        sendFile(Uri.fromFile(File(grpImageUrl)), myUserId, view.context)

//    for (i in 0 until userList.size) {
//      database.child("User").child(userList.get()).child(KEY_ADDED_GRP).setValue(key)
//    }
    }

    private fun sendFile(path: Uri, myUserId: String?, context: Context) {
        ProgressBarHandler.getInstance()
        ProgressBarHandler.show(context)
        var databaseReference = FirebaseDatabase.getInstance().reference

        val date = Calendar.getInstance().timeInMillis.toString();
        val user_message_push = databaseReference.child("group")
                .push();
        val push_id = user_message_push.getKey();
        val filePath = FirebaseStorage.getInstance().reference.child("group_images").child(push_id + ".jpg")

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
                    val database = FirebaseDatabase.getInstance()
                            .reference
                    val groupId = database.child(ConstantsValues.KEY_GROUP_DETAILS).child(grpName).push().getKey()
                    val group = HashMap<String, Any>()
                    group.put(ConstantsValues.KEY_GRP_NAME, grpName)
                    group.put(ConstantsValues.KEY_GRP_ID, groupId.toString())
                    group.put(KEY_GRP_IMAGE, download_url)
                    group.put(ConstantsValues.KEY_CREATED_BY, myUserId!!)
                    group.put(ConstantsValues.KEY_CREATED_AT, ServerValue.TIMESTAMP)

                    var groupIdList=ArrayList<String>()
                    for (i in 0 until  userList.size) {
                        groupIdList.add(userList.get(i).uid!!)

                    }
                    group.put(KEY_GRP_USER, groupIdList)
                    database.child(KEY_GROUP_DETAILS).child(groupId.toString()).setValue(group)
//                    database.child("User").child(myUserId).child(KEY_ADDED_GRP).setValue(key)
//                    val  userIterator = userList.keys.iterator();
//                    while(userIterator.hasNext()) {
                    for (i in 0 until userList.size) {
//                        val valkey=userList.next()
                        val users = userList.get(i) as User

                        FirebaseDatabase.getInstance()
                                .reference.child("Users").child(users.uid.toString()).child(ConstantsValues.KEY_ADDED_GRP).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                val childrenCunt = p0.childrenCount
                                val grpValue = HashMap<String, String>()
                                var countKey = 0L
                                if (childrenCunt > 0) {
                                    countKey = childrenCunt + 1
//                                    grpValue.put(countKey.toString(), key.toString())
                                } else
                                    countKey = 1L
                                grpValue.put(KEY_GRP_NAME, grpName)
                                grpValue.put(KEY_GRP_IMAGE, download_url)
                                grpValue.put(KEY_GRP_ID, groupId.toString())
                                database.child("Users").child(users.uid.toString()).child(KEY_ADDED_GRP).child(groupId.toString()).setValue(grpValue).addOnSuccessListener(object : OnSuccessListener<Void>{
                                    override fun onSuccess(p0: Void?) {
                                        if (i==userList.size-1)
                                            groupCreated(context)
                                    }

                                })
                                database.child("Users").child(users!!.uid.toString()).child(KEY_CREATED_BY).setValue(myUserId)
//                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                            }

                        })

                    }

                }
            }
        })

    }

    fun groupCreated(context: Context){
        ProgressBarHandler.hide()
        Toast.makeText(context, grpName + " Group create Sucessfully", Toast.LENGTH_SHORT).show()
        val intent=Intent(context,UserFragment::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
        (context as AppCompatActivity).finish()
    }
    fun getChildrent(): Int {
        FirebaseDatabase.getInstance()
                .reference.child("Users").child(user?.uid!!).child(ConstantsValues.KEY_ADDED_GRP).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.childrenCount
//                for (DataSnapshot snap: dataSnapshot.getChildren()) {
            }

        })
        return 1
    }

}