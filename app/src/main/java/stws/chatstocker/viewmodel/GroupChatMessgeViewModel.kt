package stws.chatstocker.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import org.json.JSONObject
import stws.chatstocker.ConstantsValues
import stws.chatstocker.model.ChatMessage
import stws.chatstocker.model.User
import stws.chatstocker.utils.Prefrences
import stws.chatstocker.utils.ProgressBarHandler
import java.util.*
import kotlin.collections.ArrayList

class GroupChatMessgeViewModel:ViewModel() {
    private var onchatSendResponse: MutableLiveData<ChatMessage>? = null
    private var childEventListenerResponse: MutableLiveData<ChildEventListener>? = null
    private var onchatMessageSendResponse: MutableLiveData<ChatMessage>? = null
    lateinit var childEventListener: ChildEventListener
    var sendMessageEventListener: ValueEventListener?=null
    private var room_type = "0"
    private var isCleared:Boolean=true
    private val TAG = "Chat"

    var isBlocked:Boolean=false
        set(value) {
            field = value
        }
        get() = field
    var isloadedOnce: Boolean = false
        set(value) {
            field = value
        }
        get() = field
    var message: String = ""
        set(value) {
            field = value
        }
        get() = field
    var senderUid: String = ""
        set(value) {
            field = value
        }
        get() = field
    var groupUid: String = ""
        set(value) {
            field = value
        }
        get() = field
    var groupName: String = ""
        set(value) {
            field = value
        }
        get() = field

    var name: String = ""
        get() = field
        set(value) {
            field = value
        }
    var userPic: String = ""
        get() = field
        set(value) {
            field = value
        }
    var lastSeen: String = ""
        get() = field
        set(value) {
            field = value
        }
    var imageUrl: String = ""
        get() = field
        set(value) {
            field = value
        }

    @BindingAdapter("imageUrl")
    fun loadImage(imageView: ImageView, imageUrl: String) {
        Glide.with(imageView.context).load(imageUrl).into(imageView)
    }

    fun getChatResponse(): LiveData<ChatMessage> {
        onchatMessageSendResponse = MutableLiveData<ChatMessage>()
        return onchatMessageSendResponse as MutableLiveData<ChatMessage>
    }

//    fun getAllChatResponse(): LiveData<ChatMessage> {
////        if (onchatSendResponse == null) {
//        onchatSendResponse = MutableLiveData<ChatMessage>()
//        getAllChat()
////        }
//        return onchatSendResponse as MutableLiveData<ChatMessage>
//    }

//    fun getChildEventListener(): LiveData<java.util.ArrayList<ChatMessage>> {
//        if (onchatSendResponse!=null)
//        newChildAddedListener()
//
//        return onchatSendResponse as MutableLiveData<java.util.ArrayList<ChatMessage>>
//    }

    public fun onSendClick(view: View) {
        if (message.length > 0)
            sendMessageClick(view)
    }
    fun forwardMessage(chatMessage: ChatMessage,context: Context) {
        isCleared=false

        var databaseReference = FirebaseDatabase.getInstance().reference
        val date = Calendar.getInstance().timeInMillis.toString();
//        val chat = ChatMessage(message, "flase", "text", senderUid, date,groupUid,"",false,name)

        getAllGroupUser(groupUid,context,chatMessage.msg)
        onchatMessageSendResponse!!.postValue(chatMessage)
        sendMessageEventListener=object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (isCleared)
                    return
                Log.e(TAG, "sendMessageToFirebaseUser: success")
                databaseReference.child("chat_room")
                        .child(groupUid)
                        .child(date)
                        .setValue(chatMessage)

            }

        }
        databaseReference.child("chat_room")
                .ref
                .addListenerForSingleValueEvent(sendMessageEventListener!!)

    }
    private fun sendMessageClick(view: View) {
        isCleared=false
//        if (isBlocked) {
//            Toast.makeText(view.context,"You are unable to send message to this user", Toast.LENGTH_SHORT).show()
//            return
//        }
        var databaseReference = FirebaseDatabase.getInstance().reference
//        if (room_type.equals("1")) {
//            FirebaseDatabase.getInstance()
//                    .reference
//                    .child("chat_room")
//                    .child(senderUid ).removeEventListener(childEventListener)
//        } else if (room_type.equals("2")) {
//            FirebaseDatabase.getInstance()
//                    .reference
//                    .child("chat_room")
//                    .child(receiverUid + "_" + senderUid).removeEventListener(childEventListener)
//        }

        val date = Calendar.getInstance().timeInMillis.toString();
        val chat = ChatMessage(message, "flase", "text", senderUid, date,groupUid,"",false,name)
//        val room_type_1 = senderUid + "_" + receiverUid;
//        val room_type_2 = receiverUid + "_" + senderUid;
        getAllGroupUser(groupUid,view.context,message)
        onchatMessageSendResponse!!.postValue(chat)
        sendMessageEventListener=object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (isCleared)
                    return
//                if (dataSnapshot.hasChild(room_type_1)) {
//                    Log.e(TAG, "sendMessageToFirebaseUser: $room_type_1 exists")
//
//                    databaseReference.child("chat_room")
//                            .child(room_type_1)
//                            .child(date)
//                            .setValue(chat)
////                            onchatMessageSendResponse!!.postValue(chat)
//                } else if (dataSnapshot.hasChild(room_type_2)) {
//                    Log.e(TAG, "sendMessageToFirebaseUser: $room_type_2 exists")
//                    databaseReference.child("chat_room")
//                            .child(room_type_2)
//                            .child(date)
//                            .setValue(chat)
////                            onchatMessageSendResponse!!.postValue(chat)
//                } else {
                    Log.e(TAG, "sendMessageToFirebaseUser: success")
                    databaseReference.child("chat_room")
                            .child(groupUid)
                            .child(date)
                            .setValue(chat)

//                            onchatMessageSendResponse!!.postValue(chat)
//                }

            }

        }
        databaseReference.child("chat_room")
                .ref
                .addListenerForSingleValueEvent(sendMessageEventListener!!)
//        databaseReference.child("chat_room")
//                .ref.removeEventListener(sendMessageEventListener!!)
//        databaseReference.child("chat_room")
//                .ref
//                .addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        if (dataSnapshot.hasChild(room_type_1)) {
//                            Log.e(TAG, "sendMessageToFirebaseUser: $room_type_1 exists")
//
//                            databaseReference.child("chat_room")
//                                    .child(room_type_1)
//                                    .child(date)
//                                    .setValue(chat)
////                            onchatMessageSendResponse!!.postValue(chat)
//                        } else if (dataSnapshot.hasChild(room_type_2)) {
//                            Log.e(TAG, "sendMessageToFirebaseUser: $room_type_2 exists")
//                            databaseReference.child("chat_room")
//                                    .child(room_type_2)
//                                    .child(date)
//                                    .setValue(chat)
////                            onchatMessageSendResponse!!.postValue(chat)
//                        } else {
//                            Log.e(TAG, "sendMessageToFirebaseUser: success")
//                            databaseReference.child("chat_room")
//                                    .child(room_type_1)
//                                    .child(date)
//                                    .setValue(chat)
////                            onchatMessageSendResponse!!.postValue(chat)
//                        }
//
//                    }
//
//                    override fun onCancelled(databaseError: DatabaseError) {
//                        // Unable to send message.
//                    }
//                })

    }




    fun clearChat(list:List<ChatMessage>, userId:String){
        if (sendMessageEventListener!=null)
            FirebaseDatabase.getInstance().reference.child("chat_room")
                    .ref
                    .removeEventListener(sendMessageEventListener!!)

        for (i in 0 until list.size) {
            FirebaseDatabase.getInstance().reference.child("chat_room").child(groupUid).child(list.get(i).date)
                    .child(userId).setValue(true)
        }
    }

    fun exitGroup(){
        FirebaseDatabase.getInstance().reference.child("User")
                .ref.child(senderUid).child(ConstantsValues.KEY_ADDED_GRP).child(groupUid).removeValue()
        FirebaseDatabase.getInstance().reference.child(ConstantsValues.KEY_GROUP_DETAILS).child(groupUid)
               .child(ConstantsValues.KEY_GRP_USER).addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(groupDataSnapshot: DataSnapshot) {
                        val children:Iterable<DataSnapshot> = groupDataSnapshot.children
                        for (group:DataSnapshot in children) {
                            val key = group.key
                            val value=group.value
                            if (senderUid.equals(value)){
                                FirebaseDatabase.getInstance().reference.child(ConstantsValues.KEY_GROUP_DETAILS).child(groupUid)
                                        .child(ConstantsValues.KEY_GRP_USER).child(key.toString()).removeValue()
                                break
                            }
                        }
                    }

                })
    }





//    private fun newChildAddedListener() {
//
//        if (room_type == "0") {
////            if (childEventListener!= null)
////            FirebaseDatabase.getInstance().reference.child("chat_room").child(senderUid + "_" + receiverUid).removeEventListener(childEventListener)
//            FirebaseDatabase.getInstance().reference.child("chat_room").child(senderUid + "_" + receiverUid).orderByKey().limitToLast(1)
//                    .addValueEventListener(object : ValueEventListener {
//                        override fun onCancelled(p0: DatabaseError) {
//
//                        }
//
//                        override fun onDataChange(dataSnapshot: DataSnapshot) {
//                            if (dataSnapshot.value != null) {
//                                val chatMessage = dataSnapshot.getValue(ChatMessage::class.java!!)
//                                chatMessage!!.date = dataSnapshot.key.toString()
//
//                                onchatMessageSendResponse!!.postValue(chatMessage)
//                            }
//                        }
//                    })
//        }
//        else{
////            if (childEventListener!= null)
////            FirebaseDatabase.getInstance().reference.child("chat_room").child(receiverUid + "_" + senderUid).removeEventListener(childEventListener)
//            FirebaseDatabase.getInstance().reference.child("chat_room").child(receiverUid + "_" + senderUid).orderByKey().limitToLast(1)
//                    .addValueEventListener(object : ValueEventListener {
//                        override fun onCancelled(p0: DatabaseError) {
//
//                        }
//
//                        override fun onDataChange(dataSnapshot: DataSnapshot) {
//                            if (dataSnapshot.value != null) {
//                                val chatMessage = dataSnapshot.getValue(ChatMessage::class.java!!)
//                                chatMessage!!.date = dataSnapshot.key.toString()
//
//                                onchatMessageSendResponse!!.postValue(chatMessage)
//                            }
//                        }
//                    })
//        }
//    }

    fun unregisterEventListener() {

        if (sendMessageEventListener!=null)
            FirebaseDatabase.getInstance().reference.child("chat_room")
                    .ref
                    .removeEventListener(sendMessageEventListener!!)
    }

    fun sendNotifcationtoUser(title:String,msg:String,recieverdeviceToken:String,context: Context){
        val jsonObject= JSONObject()
        jsonObject.put("to",recieverdeviceToken)
        jsonObject.put("priority","high")
        val jsonObjectNotification= JSONObject()
        jsonObjectNotification.put("title",title)
        jsonObjectNotification.put("body",msg)
        jsonObject.put("notification",jsonObjectNotification)
        sendNotification(jsonObject,context)
    }

    private fun sendNotification(notification: JSONObject, context: Context) {
        Log.e("TAG", "sendNotification")
        val requestQueue= Volley.newRequestQueue(context)
        val jsonObjectRequest = object : JsonObjectRequest(ConstantsValues.FCM_API, notification,
                Response.Listener<JSONObject> { response ->
                    Log.i("TAG", "onResponse: $response")

                },
                Response.ErrorListener {
                    Toast.makeText(context, "Request error", Toast.LENGTH_LONG).show()
                    Log.i("TAG", "onErrorResponse: Didn't work")
                }) {

            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = ConstantsValues.SERVER_KEY
                params["Content-Type"] = "application/json"
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
    }

    fun getAllGroupUser(grpId:String,context: Context,msg: String){

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
                                    if (ds.hasChild(ConstantsValues.KEY_DEVICE_TOKEN)) {
                                        val token = ds.child(ConstantsValues.KEY_DEVICE_TOKEN).getValue(String::class.java)
                                        if (!Prefrences.getUserDetails(context,ConstantsValues.KEY_LOGIN_DATA).deviceToken.equals(token))
                                    sendNotifcationtoUser(groupName,msg,token!!,context)
                                    }
                                }
                            })
                        }
                    }

                    override fun onCancelled(p0: DatabaseError) {

                    }



                })
    }
}