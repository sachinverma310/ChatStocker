package stws.chatstocker.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import stws.chatstocker.ConstantsValues
import stws.chatstocker.model.GroupUserList
import stws.chatstocker.model.User
import stws.chatstocker.utils.Prefrences
import stws.chatstocker.utils.ProgressBarHandler
import stws.chatstocker.view.GorupChatUserListActivity
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class EditGroupViewModel:ViewModel, ConstantsValues {
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
    var createdByName:ObservableField<String>?= ObservableField()
    set(value) {
        field=value
    }
    get() = field
    var grpId:String=""
    set(value) {
        field=value
    }
    get() = field

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
    lateinit var context:Context;
    lateinit var userListMutable:MutableLiveData<ArrayList<User>>
    lateinit var isAddedToGroup:MutableLiveData<Boolean>
    fun addUserToGroup(user:ArrayList<GroupUserList>,groupId:String,size:Int):LiveData<Boolean>{
        isAddedToGroup= MutableLiveData()
        editGroup(user,groupId,size)
        return isAddedToGroup
    }
    fun getUserList(grpId: String,  context:Context):LiveData<ArrayList<User>>{
        userListMutable= MutableLiveData()
        this.context=context
        callUserListApi(grpId,context)
        return userListMutable
    }
    fun createGroup(view: View) {
        if (grpName.length == 0)
            return
        FirebaseDatabase.getInstance()
                .reference.child(ConstantsValues.KEY_GROUP_DETAILS).child(grpId).child(ConstantsValues.KEY_GRP_NAME).setValue(grpName)
        for (i in 0 until userList.size)
            FirebaseDatabase.getInstance()
                    .reference.child("User").child(userList.get(i).uid!!).child(ConstantsValues.KEY_ADDED_GRP)
                    .child(grpId).child(ConstantsValues.KEY_GRP_NAME).setValue(grpName)

    }

     fun sendFile(path: Uri, context: Context) {
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
                    FirebaseDatabase.getInstance()
                            .reference.child(ConstantsValues.KEY_GROUP_DETAILS).child(grpId).child(ConstantsValues.KEY_GRP_IMAGE).setValue(download_url)

                    for (i in 0 until userList.size)
                        FirebaseDatabase.getInstance()
                                .reference.child("User").child(userList.get(i).uid!!).child(ConstantsValues.KEY_ADDED_GRP)
                                .child(grpId).child(ConstantsValues.KEY_GRP_IMAGE).setValue(download_url)

                    ProgressBarHandler.hide()
                    Toast.makeText(context, grpName + " Group created Sucessfully", Toast.LENGTH_SHORT).show()

                }
            }
        })

    }

   private fun editGroup(user:ArrayList<GroupUserList>,groupId:String,size:Int) {
        val database = FirebaseDatabase.getInstance()
                .reference
        var groupSize=size-1;
       ProgressBarHandler.show(context)
        for (i in 0 until user.size) {
            groupSize=groupSize+1
            database.child(ConstantsValues.KEY_GROUP_DETAILS).child(groupId).child(ConstantsValues.KEY_GRP_USER).child(groupSize.toString()).setValue(user.get(i).user!!.uid)
            database.child("User").child(user.get(i).user!!.uid!!).child(ConstantsValues.KEY_ADDED_GRP).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
//                    val childrenCunt = p0.childrenCount
                    val grpValue = HashMap<String, String>()
////                    var countKey = 0L
////                    if (childrenCunt > 0) {
////                        countKey = childrenCunt + 1
//////                                    grpValue.put(countKey.toString(), key.toString())
////                    } else
////                        countKey = 1L
                    grpValue.put(ConstantsValues.KEY_GRP_NAME, grpName)
                    grpValue.put(ConstantsValues.KEY_GRP_IMAGE, user.get(i).user!!.image!!)
                    grpValue.put(ConstantsValues.KEY_GRP_ID, groupId.toString())
                    database.child("User").child(user.get(i).user!!.uid!!).child(ConstantsValues.KEY_ADDED_GRP).child(groupId.toString()).setValue(grpValue)
                    isAddedToGroup.postValue(true)
                    if (i==user.size-1)
                    ProgressBarHandler.hide()
//                database.child("User").child(userId).child(ConstantsValues.KEY_CREATED_BY).setValue(myUserId)
//                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                }

            })
        }

    }
    fun callUserListApi(grpId:String,  context:Context){
        ProgressBarHandler.getInstance()
        ProgressBarHandler.show(context)
        FirebaseDatabase.getInstance()
                .reference.child(ConstantsValues.KEY_GROUP_DETAILS).child(grpId).child(ConstantsValues.KEY_GRP_USER)
                .addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(p0: DataSnapshot) {
                        val childrenCunt = p0.childrenCount
                        val children:Iterable<DataSnapshot> = p0.children
                        for (group:DataSnapshot in children) {
                            val groupNo = group.key
                            val userId = group.value
                            Log.e("user",userId.toString())
                            FirebaseDatabase.getInstance()
                                    .reference.child("User").child(userId.toString()!!).addListenerForSingleValueEvent(object :ValueEventListener{
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(ds: DataSnapshot) {
                                        val uid = ds.child("uid").getValue(String::class.java)
                                        val createdBy= ds.child("created_by").getValue(String::class.java)
                                        val name = ds.child("name").getValue(String::class.java)
                                    if (uid.equals(createdBy))
                                   createdByName!!.set(name)
                                        val email = ds.child("email").getValue(String::class.java)
                                        var status:Boolean?=false
                                        if (ds.hasChild("online"))
                                            status = ds.child("online").getValue(Boolean::class.java)
                                        val profileImage = ds.child("profileImage").getValue(String::class.java)
                                        val lastSeen = ds.child("lastSeen").getValue(String::class.java)
//                    val uid = ds.getValue().toString()
                                        var user:User?=null;
                                        if (status!!)
                                            user = User(name, profileImage, true, email, lastSeen, uid,false,false)
                                        else
                                            user = User(name, profileImage, false, email, lastSeen, uid,false,false)
//                        Log.d("TAG", name)
                                        userList.add(user)
                                    userListMutable.postValue(userList)
                                    ProgressBarHandler.hide()
                                }
                            })
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }



                })
    }

    fun addParticipent(view: View){
        val intent=Intent(view.context, GorupChatUserListActivity::class.java)
        intent.putParcelableArrayListExtra(ConstantsValues.KEY_GROUP_DETAILS,userList)
        ( context as AppCompatActivity).startActivityForResult (intent,ConstantsValues.KEY_EDIT_REQUEST_CODE)
    }

}