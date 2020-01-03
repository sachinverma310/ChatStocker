package stws.chatstocker.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.R.array
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.Contacts
import android.provider.ContactsContract
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.kartikonlinewholeseller.apisrepositories.ApiClient
import com.kartikonlinewholeseller.apisrepositories.ApiService
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.internal.notify
import org.json.JSONException
import stws.chatstocker.ConstantsValues
import stws.chatstocker.model.Friends
import stws.chatstocker.model.GroupDetails
import stws.chatstocker.model.User
import stws.chatstocker.utils.Prefrences
import java.util.*
import kotlin.collections.ArrayList


class UserListViewModel() : ViewModel(), ConstantsValues {
    private lateinit var nameArrayList: ArrayList<User>
    private lateinit var contactList: MutableList<String>
    //    var user: User?=null
//        get() = field
//        set(value) {
//            field = value
//        }
//    var email: String?
//    var name: String?
//    var phone: String?
//    var profilePic: String?
//    var onlineStatus: String
    private var list: MutableLiveData<ArrayList<User>>? = null
    private lateinit var friendList: ArrayList<Friends>

    //    init {
//        this.user = user
//        this.profilePic = user!!.image
//        this.email = user!!.email
//        this.phone = user!!.email
//        this.name = user!!.name
//        this.onlineStatus=user!!.online.toString()
//    }
    private lateinit var context: Context

    public fun userList(context: Context,isFromGroup:Boolean): LiveData<ArrayList<User>>? {
        this.context = context;
//        if (list == null) {
            list = MutableLiveData()
            friendListing(isFromGroup)
//            userListing()

//        }
        return list
    }

    public fun getGroupList(context: Context): LiveData<ArrayList<User>>? {
        this.context = context;

//            groupListing()

        return list
    }

    private fun getContactsListing() {
        contactList = ArrayList<String>()
        val contacts = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        // Empty text view


        // Loop through the contacts
        while (contacts!!.moveToNext()) {
            // Get the current contact name
            val name = contacts.getString(
                    contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));

            // Get the current contact phone number
            val phoneNumber = contacts.getString(
                    contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim();
            if (phoneNumber.contains("+91"))
                contactList.add(phoneNumber.replace(" ", ""))
            else
                contactList.add("+91" + phoneNumber.replace(" ", ""))
            // Display the contact to text view


        }
        contacts.close();
    }

    private fun friendListing(isFromGroup:Boolean) {
        getContactsListing()
        friendList = ArrayList()
        nameArrayList = ArrayList<User>()
//        var filteredList: ArrayList<User> = ArrayList<User>()
        val myuid = Prefrences.Companion.getUserDetails(context, ConstantsValues.KEY_LOGIN_DATA).uid
        val apiService =
                ApiClient.getRetrofitWithoutAuth(context)!!.create(ApiService::class.java)
        apiService.listuser().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<JsonObject> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: JsonObject) {
                        val jsonObject = t.getAsJsonObject(myuid)
                        if (jsonObject.has("friend")) {
                            val jsonObjectFriend = jsonObject.getAsJsonObject("friend")
                            val entries: Set<Map.Entry<String, JsonElement>> = jsonObjectFriend.entrySet();//will return members of your object
                            for (entry: Map.Entry<String, JsonElement> in entries) {
                                System.out.println(entry.key);
                                val jsonObjectKey = jsonObjectFriend.getAsJsonObject(entry.key)
                                val lastMsgTime = jsonObjectKey.get("last_msg_time").asString
                                var friends = Friends(lastMsgTime!!, entry.key!!)
                                friendList.add(friends)
                            }
                            Collections.sort(friendList)
                            Log.e("sorted", friendList.toString())
                        }

                        val mainentries: Set<Map.Entry<String, JsonElement>> = t.entrySet();
                        for (entry: Map.Entry<String, JsonElement> in mainentries) {
                            val jsonObjectKey = t.getAsJsonObject(entry.key)
                            var createdBy =""
                            if (jsonObjectKey.has("created_by"))
                             createdBy = jsonObjectKey.get("created_by").asString
                            val device_token = jsonObjectKey.get("device_token").asString
                            val email = jsonObjectKey.get("email").asString
                            val lastSeen = jsonObjectKey.get("lastSeen").asString
                            val name = jsonObjectKey.get("name").asString
                            val numbers = jsonObjectKey.get("numbers").asString
                            val online = jsonObjectKey.get("online").asBoolean
                            val profileImage = jsonObjectKey.get("profileImage").asString
                            val uid = jsonObjectKey.get("uid").asString
                            var user: User? = null;
                            if (online!!)
                                user = User(name, profileImage, true, email, lastSeen, uid, false, false)
                            else
                                user = User(name, profileImage, false, email, lastSeen, uid, false, false)
                            user.deviceToken = device_token
                            if (contactList.contains(numbers)) {
                                if (!myuid.equals(uid))
                                    nameArrayList.add(user)
                            }
                            if (!isFromGroup) {
                                if (jsonObjectKey.has("group")) {
                                    val jsonGroup = jsonObjectKey.getAsJsonObject("group")
                                    val groupEntries: Set<Map.Entry<String, JsonElement>> = jsonGroup.entrySet();
                                    for (groupentry: Map.Entry<String, JsonElement> in groupEntries) {
                                        val jsonObjectGroupKey = jsonGroup.getAsJsonObject(groupentry.key)
                                        val grp_id = jsonObjectGroupKey.get("grp_id").asString
                                        val grp_image = jsonObjectGroupKey.get("grp_image").asString
                                        val grp_name = jsonObjectGroupKey.get("grp_name").asString
                                        var user: User = User(grp_name, grp_image, false, "", "", grp_id, false, true)
                                        if (myuid.equals(uid))
                                            nameArrayList.add(user)
                                    }
                                }
                            }

                        }
//                        var loopCount = nameArrayList.size;
//                        filteredList.addAll(nameArrayList)
//                        var j = 0;
//                        var loopExecutedCount=0;
////                        var filterPos=0;
                        for ( j in 0 until  nameArrayList.size) {
//                            loopExecutedCount++;
                            for (i in 0 until friendList.size)
//                                if (j < loopCount) {
                                    if (friendList.get(i).uid.equals(nameArrayList.get(j).uid)) {
//                                        filteredList.remove(nameArrayList.get(j))
//                                        filteredList.add(friendList.indexOf(friendList.get(i)), nameArrayList.get(j))
//                                        filterPos++;
//                                        nameArrayList.removeAt(j)
                                        nameArrayList.get(j).lastMsgSent=friendList.get(i).time
//                                        loopCount--;


//                                    }
                                }
                        }


//                        while (j < nameArrayList.size &&loopExecutedCount!=loopCount) {
//                            loopExecutedCount++;
//                            for (i in 0 until friendList.size)
////                                if (j < loopCount) {
//                                if (friendList.get(i).uid.equals(nameArrayList.get(j).uid)) {
//                                    filteredList.remove(nameArrayList.get(j))
//                                    filteredList.add(friendList.indexOf(friendList.get(i)), nameArrayList.get(j))
////                                        filterPos++;
//                                    nameArrayList.removeAt(j)
////                                        loopCount--;
//
//
////                                    }
//                                }
//                        }
//                        filteredList.addAll(nameArrayList)
                        Collections.sort(nameArrayList)
                        list?.postValue(nameArrayList)
                    }

                    override fun onError(e: Throwable) {
                    }

                })
    }

    private fun userListing() {
        nameArrayList = ArrayList<User>()
        getContactsListing()
//        Collections.synchronizedList(nameArrayList)
        val rootRef = FirebaseDatabase.getInstance().getReference()
        val myuid = Prefrences.Companion.getUserDetails(context, ConstantsValues.KEY_LOGIN_DATA).uid
        val usersdRef = rootRef.child("User")
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.e("Count ", "" + dataSnapshot.getChildrenCount());
                for (ds in dataSnapshot.getChildren()) {
                    val uid = ds.child("uid").getValue(String::class.java)
                    if (!myuid.equals(uid)) {
                        val name = ds.child("name").getValue(String::class.java)
                        val email = ds.child("email").getValue(String::class.java)
                        val numbers = ds.child("numbers").getValue(String::class.java)
                        var status: Boolean? = false
                        if (ds.hasChild("online"))
                            status = ds.child("online").getValue(Boolean::class.java)
                        val profileImage = ds.child("profileImage").getValue(String::class.java)
                        val lastSeen = ds.child("lastSeen").getValue(String::class.java)

//                    val uid = ds.getValue().toString()
                        var user: User? = null;
                        if (status!!)
                            user = User(name, profileImage, true, email, lastSeen, uid, false, false)
                        else
                            user = User(name, profileImage, false, email, lastSeen, uid, false, false)
//                        Log.d("TAG", name)
                        if (ds.hasChild(ConstantsValues.KEY_DEVICE_TOKEN)) {
                            val deviceToken = ds.child(ConstantsValues.KEY_DEVICE_TOKEN).getValue(String::class.java)
                            user.deviceToken = deviceToken
                        }
                        if (contactList.contains(numbers))
                            nameArrayList.add(user)
                    }


                }


                list?.postValue(nameArrayList)


            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("error", databaseError.message)
            }
        }
        usersdRef.addListenerForSingleValueEvent(eventListener)
    }

    private fun groupListing() {
//        nameArrayList = ArrayList<User>()
        val rootRef = FirebaseDatabase.getInstance().getReference()
        val myuid = Prefrences.Companion.getUserDetails(context, ConstantsValues.KEY_LOGIN_DATA).uid
        val usersdRef = rootRef.child("User").child(myuid!!)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild(ConstantsValues.KEY_ADDED_GRP)) {
                    val groupDataSnapshot = dataSnapshot.child(ConstantsValues.KEY_ADDED_GRP)
                    val children: Iterable<DataSnapshot> = groupDataSnapshot.children
                    for (group: DataSnapshot in children) {
                        val groupNo = group.key
                        val grpName = group.child(ConstantsValues.KEY_GRP_NAME).getValue(String::class.java)
                        val id = group.child(ConstantsValues.KEY_GRP_ID).getValue(String::class.java);
                        val image = group.child(ConstantsValues.KEY_GRP_IMAGE).getValue(String::class.java);
                        var user: User = User(grpName, image, false, "", "", id, false, true)
                        nameArrayList.add(user)
//                        val groupvalue=group.child(groupNo!!)
//                        for (groupData:DataSnapshot in groupvalue.children){
//                            val name = groupData.child(ConstantsValues.KEY_GRP_NAME).getValue();
//                            val id = groupData.child(ConstantsValues.KEY_GRP_ID).getValue();
//                            val image = groupData.child(ConstantsValues.KEY_GRP_IMAGE).getValue();
//                        }
                        Log.e("grp", groupNo)
                    }
                    Collections.sort(nameArrayList)
                    list?.postValue(nameArrayList)
                }
//                for (ds in dataSnapshot.getChildren()) {
//                    val uid = ds.child("uid").getValue(String::class.java)
//                    if (!myuid.equals(uid)) {
//                        val name = ds.child("name").getValue(String::class.java)
//                        val email = ds.child("email").getValue(String::class.java)
//                        var status:Boolean?=false
//                        if (ds.hasChild("online"))
//                            status = ds.child("online").getValue(Boolean::class.java)
//                        val profileImage = ds.child("profileImage").getValue(String::class.java)
//                        val lastSeen = ds.child("lastSeen").getValue(String::class.java)
////                    val uid = ds.getValue().toString()
//                        var user:User?=null;
//                        if (status!!)
//                            user = User(name, profileImage, true, email, lastSeen, uid,false)
//                        else
//                            user = User(name, profileImage, false, email, lastSeen, uid,false)
//                        Log.d("TAG", name)
//                        nameArrayList.add(user)
//                    }
//
//                }
//                list?.postValue(nameArrayList)


            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("error", databaseError.message)
            }
        }
        usersdRef.addListenerForSingleValueEvent(eventListener)
    }
}