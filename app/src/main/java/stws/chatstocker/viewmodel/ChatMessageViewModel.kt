package stws.chatstocker.viewmodel

import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import stws.chatstocker.model.ChatMessage
import java.util.*
import kotlin.collections.HashMap
import com.google.firebase.database.DatabaseError
import androidx.databinding.adapters.NumberPickerBindingAdapter.setValue
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ChildEventListener

import android.R.attr.name
import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.gson.JsonObject
import org.json.JSONObject
import stws.chatstocker.ConstantsValues
import stws.chatstocker.ConstantsValues.SERVER_KEY
import kotlin.collections.ArrayList


class ChatMessageViewModel : ViewModel() {
    private var onchatSendResponse: MutableLiveData<ChatMessage>? = null
    private var childEventListenerResponse: MutableLiveData<ChildEventListener>? = null
    private var onchatMessageSendResponse: MutableLiveData<ChatMessage>? = null
    lateinit var childEventListener: ChildEventListener
    var sendMessageEventListener: ValueEventListener?=null
    public var room_type = "1"
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
    var receiverUid: String = ""
        set(value) {
            field = value
        }
        get() = field
    var receiverName: String = ""
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
    var recieverdeviceToken: String = ""
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

    private fun sendMessageClick(view: View) {
        isCleared=false
            if (isBlocked) {
                Toast.makeText(view.context,"You are unable to send message to this user",Toast.LENGTH_SHORT).show()
                return
            }
        var databaseReference = FirebaseDatabase.getInstance().reference
//        if (room_type.equals("1")) {
//            FirebaseDatabase.getInstance()
//                    .reference
//                    .child("chat_room")
//                    .child(senderUid + "_" + receiverUid).removeEventListener(childEventListener)
//        } else if (room_type.equals("2")) {
//            FirebaseDatabase.getInstance()
//                    .reference
//                    .child("chat_room")
//                    .child(receiverUid + "_" + senderUid).removeEventListener(childEventListener)
//        }

        val date = Calendar.getInstance().timeInMillis.toString();
        val chat = ChatMessage(message, "flase", "text", senderUid, date,receiverUid,"",false,name)
       var room_type_1=""
        if (room_type.equals("2"))
            room_type_1 = receiverUid + "_" + senderUid;

        else
            room_type_1 = senderUid + "_" + receiverUid;


        onchatMessageSendResponse!!.postValue(chat)
//        sendMessageEventListener=object :ValueEventListener{
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (isCleared)
//                    return
//                if (dataSnapshot.hasChild(room_type_1)) {
//                    Log.e(TAG, "sendMessageToFirebaseUser: $room_type_1 exists")

                    databaseReference.child("chat_room")
                            .child(room_type_1)
                            .child(date)
                            .setValue(chat).addOnSuccessListener(object:OnSuccessListener<Void>{
                                override fun onSuccess(p0: Void?) {
                                    sendNotifcationtoUser(receiverName,message,view.context,date,room_type_1)
                                }

                            })
//                            onchatMessageSendResponse!!.postValue(chat)
//                } else if (dataSnapshot.hasChild(room_type_2)) {
//                    Log.e(TAG, "sendMessageToFirebaseUser: $room_type_2 exists")
//                    databaseReference.child("chat_room")
//                            .child(room_type_2)
//                            .child(date)
//                            .setValue(chat)
//                            onchatMessageSendResponse!!.postValue(chat)
//                } else {
//                    Log.e(TAG, "sendMessageToFirebaseUser: success")
//                    databaseReference.child("chat_room")
//                            .child(room_type_1)
//                            .child(date)
//                            .setValue(chat)
//                            onchatMessageSendResponse!!.postValue(chat)
//                }

//            }
//
//        }
//        databaseReference.child("chat_room")
//                .ref
//                .addValueEventListener(sendMessageEventListener!!)
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

     fun forwardMessage(chatMessage: ChatMessage,context: Context) {
        isCleared=false
        if (isBlocked) {
            Toast.makeText(context,"You are unable to send message to this user",Toast.LENGTH_SHORT).show()
            return
        }
        var databaseReference = FirebaseDatabase.getInstance().reference
//        if (room_type.equals("1")) {
//            FirebaseDatabase.getInstance()
//                    .reference
//                    .child("chat_room")
//                    .child(senderUid + "_" + receiverUid).removeEventListener(childEventListener)
//        } else if (room_type.equals("2")) {
//            FirebaseDatabase.getInstance()
//                    .reference
//                    .child("chat_room")
//                    .child(receiverUid + "_" + senderUid).removeEventListener(childEventListener)
//        }

        val date = Calendar.getInstance().timeInMillis.toString();
//        val chat = ChatMessage(message, "flase", "text", senderUid, date,receiverUid,"",false,name)
        var room_type_1=""
        if (room_type.equals("2"))
            room_type_1 = receiverUid + "_" + senderUid;

        else
            room_type_1 = senderUid + "_" + receiverUid;




        databaseReference.child("chat_room")
                .child(room_type_1)
                .child(date)
                .setValue(chatMessage).addOnSuccessListener(object:OnSuccessListener<Void>{
                    override fun onSuccess(p0: Void?) {
                        sendNotifcationtoUser(receiverName,message,context,date,room_type_1)
                    }

                })
//                            onchatMessageSendResponse!!.postValue(chat)
//                } else if (dataSnapshot.hasChild(room_type_2)) {
//                    Log.e(TAG, "sendMessageToFirebaseUser: $room_type_2 exists")
//                    databaseReference.child("chat_room")
//                            .child(room_type_2)
//                            .child(date)
//                            .setValue(chat)
//                            onchatMessageSendResponse!!.postValue(chat)
//                } else {
//                    Log.e(TAG, "sendMessageToFirebaseUser: success")
//                    databaseReference.child("chat_room")
//                            .child(room_type_1)
//                            .child(date)
//                            .setValue(chat)
//                            onchatMessageSendResponse!!.postValue(chat)
//                }

//            }
//
//        }
//        databaseReference.child("chat_room")
//                .ref
//                .addValueEventListener(sendMessageEventListener!!)
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
    fun getAllChat() {

        val list = ArrayList<ChatMessage>()
        val room_type_1 = senderUid + "_" + receiverUid
        val room_type_2 = receiverUid + "_" + senderUid
        val databaseReference = FirebaseDatabase.getInstance()
                .reference
        databaseReference.child("chat_room")
                .ref
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.hasChild(room_type_1)) {
                            room_type = "0"
                          childEventListener=  object : ChildEventListener {
                                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
//                                    list.clear()
                                    if (dataSnapshot.value != null) {
//                                        for (snapshot: DataSnapshot in dataSnapshot.children) {
                                            val chatMessage = dataSnapshot.getValue(ChatMessage::class.java!!)
                                            chatMessage!!.date = dataSnapshot.key.toString()
                                        onchatSendResponse!!.postValue(chatMessage)
//                                            list.add(chatMessage)

//                                        }


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

                            }
                            FirebaseDatabase.getInstance().reference.child("chat_room").child(room_type_1).addChildEventListener(childEventListener)
                        } else if (dataSnapshot.hasChild(room_type_2)) {
//                            list.clear()
                            room_type = "1"
                            childEventListener = object : ChildEventListener {
                                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                                    if (dataSnapshot.value != null) {
//                                        for (snapshot: DataSnapshot in dataSnapshot.children) {
                                            val chatMessage = dataSnapshot.getValue(ChatMessage::class.java!!)
                                            chatMessage!!.date = dataSnapshot.key.toString()
                                        onchatSendResponse!!.postValue(chatMessage)
//                                            list.add(chatMessage)

//                                        }

                                    }
//                                    onchatSendResponse!!.postValue(list)

                                }

                                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

                                }

                                override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                                }

                                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

                                }

                                override fun onCancelled(databaseError: DatabaseError) {

                                }
                            }
                            FirebaseDatabase.getInstance().reference.child("chat_room").child(room_type_2).addChildEventListener(childEventListener)
                            FirebaseDatabase.getInstance().reference.child("chat_room").child(room_type_2).removeEventListener(childEventListener)
                        }}

                })
    }

     fun clearChat(list:List<ChatMessage>,userId:String,room_type:String){
         if (sendMessageEventListener!=null)
         FirebaseDatabase.getInstance().reference.child("chat_room")
                 .ref
                 .removeEventListener(sendMessageEventListener!!)

         for (i in 0 until list.size) {
             FirebaseDatabase.getInstance().reference.child("chat_room").child(room_type).child(list.get(i).date)
                     .child(userId).setValue(true)
         }
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
 fun sendNotifcationtoUser(title:String,msg:String,context:Context,date:String,room_type: String){
     val jsonObject= JSONObject()
     jsonObject.put("to",recieverdeviceToken)
     jsonObject.put("priority","high")
     val jsonObjectNotification=JSONObject()
     jsonObjectNotification.put("title",title)
     val jsonObjectBody=JSONObject()
//     if (room_type.equals("1"))
//     jsonObjectBody.put("room_type",senderUid + "_" + receiverUid)
//     else
         jsonObjectBody.put("room_type", room_type)

     jsonObjectBody.put("recieverUid",receiverUid)
     jsonObjectBody.put("senderUid",senderUid)
     jsonObjectBody.put("msg",msg)
     jsonObjectBody.put("dateTime",date);
     jsonObjectNotification.put("body",jsonObjectBody)
     val jsonObjectData=JSONObject()
     jsonObjectData.put("body",jsonObjectBody)
     jsonObjectData.put("title",title)
//     jsonObjectData.put("recieverUid",receiverUid)
//     jsonObjectData.put("senderUid",senderUid)
//     jsonObjectData.put("msg",msg)
     jsonObject.put("data",jsonObjectData)

//     jsonObject.put("notification",jsonObjectNotification)
     sendNotification(jsonObject,context)
 }

    private fun sendNotification(notification: JSONObject,context:Context) {
        Log.e("TAG", "sendNotification")
        val requestQueue=Volley.newRequestQueue(context)
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
                params["Authorization"] = SERVER_KEY
                params["Content-Type"] = "application/json"
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
    }

}