package stws.chatstocker.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.R.array
import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import stws.chatstocker.ConstantsValues
import stws.chatstocker.model.User
import stws.chatstocker.utils.Prefrences


class UserListViewModel() : ViewModel(),ConstantsValues{
    private lateinit var nameArrayList: ArrayList<User>
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

//    init {
//        this.user = user
//        this.profilePic = user!!.image
//        this.email = user!!.email
//        this.phone = user!!.email
//        this.name = user!!.name
//        this.onlineStatus=user!!.online.toString()
//    }
private lateinit var context:Context

    public fun userList( context: Context): LiveData<ArrayList<User>>? {
        this.context=context;
        if (list == null) {
            list = MutableLiveData()
            userListing()
        }
        return list
    }

    private fun userListing() {

        nameArrayList = ArrayList<User>()
        val rootRef = FirebaseDatabase.getInstance().getReference()
    val myuid=Prefrences.Companion.getUserDetails(context, ConstantsValues.KEY_LOGIN_DATA).uid
        val usersdRef = rootRef.child("User")
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.getChildren()) {
                    val uid = ds.child("uid").getValue(String::class.java)
                    if (!myuid.equals(uid)) {
                        val name = ds.child("name").getValue(String::class.java)
                        val email = ds.child("email").getValue(String::class.java)
                        var status:Boolean?=false
                        if (ds.hasChild("online"))
                         status = ds.child("online").getValue(Boolean::class.java)
                        val profileImage = ds.child("profileImage").getValue(String::class.java)
                        val lastSeen = ds.child("lastSeen").getValue(String::class.java)
//                    val uid = ds.getValue().toString()
                        var user:User?=null;
                        if (status!!)
                         user = User(name, profileImage, true, email, lastSeen, uid)
                        else
                            user = User(name, profileImage, false, email, lastSeen, uid)
                        Log.d("TAG", name)
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

}