package stws.chatstocker.view

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView



import com.google.firebase.database.DatabaseReference

import com.google.firebase.auth.FirebaseAuth


import com.google.firebase.database.FirebaseDatabase
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.ServerValue
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.model.LoginResponse
import stws.chatstocker.utils.Prefrences
import java.io.IOException
import java.util.*


class ChatUserActivity : BaseActivity() {
    private val mauth: FirebaseAuth? = null
    var mDatabaseReference: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_user)
        val recyclerView=findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.itemAnimator=DefaultItemAnimator()
        recyclerView.layoutManager=LinearLayoutManager(this)
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
//        recyclerView.adapter=UserAdapter()
    }

    override fun onStart() {
        super.onStart()
        val user = mauth!!.getCurrentUser()
        if (user == null) {
//            startfn()
        } else {
            //---IF LOGIN , ADD ONLINE VALUE TO TRUE---
            mDatabaseReference!!.child(user!!.getUid()).child("online").setValue("true")

        }
    }

    override fun onDestroy() {
        val user=mauth!!.getCurrentUser();
        if(user!=null){
            mDatabaseReference!!.child(user.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
        }
        super.onDestroy()
    }
    override fun onStop() {
        super.onStop()

       //-----for disabling online function when appliction runs in background----
        val user=mauth!!.getCurrentUser();
        if(user!=null){
            mDatabaseReference!!.child(user.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
        }

    }

//    private fun updateUi() {
//
//        var loginResponse:LoginResponse
//        try {
//          loginResponse=  Prefrences.getUserDetails(this, ConstantsValues.KEY_LOGIN_DATA)
//
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//        val addValue = HashMap<String,Any>()
//        addValue.put("device_token", loginResponse.e)
//        addValue.put("online", true)
//        addValue.put("name", user.displayName!!)
//        addValue.put("email", user.email!!)
//        addValue.put("numbers", user.phoneNumber!!)
//        addValue.put("profileImage", user.photoUrl!!.toString())
//        addValue.put("lastSeen", Calendar.getInstance().time.toString())
//        addValue.put("uid", user.uid)
//        //---IF UPDATE IS SUCCESSFULL , THEN OPEN MAIN ACTIVITY---
//        FirebaseDatabase.getInstance().reference.child(user.uid).updateChildren(addValue, DatabaseReference.CompletionListener { databaseError, databaseReference ->
//            if (databaseError == null) {
//
//                //---OPENING MAIN ACTIVITY---
//                Log.e("Login : ", "Logged in Successfully")
//                Prefrences.saveBoolean(this@LoginActivity, ConstantsValues.KEY_IS_LOGIN, true)
//                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
//            } else {
//                Toast.makeText(this@LoginActivity, databaseError.toString(), Toast.LENGTH_SHORT).show()
//                Log.e("Error is : ", databaseError.toString())
//
//            }
//        })
//        //        mUserDatabase.child("letters").push().setValue("a");
//        //        mUserDatabase.child("letters").push().setValue("z");
//        //        mUserDatabase.child("letters").push().setValue("c");
//
//        //        mUserDatabase.child(user.getUid()).child("device_token").setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
//        //            @Override
//        //            public void onSuccess(Void aVoid) {
//        //                Prefrences.Companion.saveBoolean(LoginActivity.this,KEY_IS_LOGIN,true);
//        //                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
//        //
//        //
//        //            }
//        //        });
//        //        ChildEventListener childEventListener = new ChildEventListener() {
//        //            @Override
//        //            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//        //                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
//        //
//        //                // A new comment has been added, add it to the displayed list
//        //                Comment comment = dataSnapshot.getValue(Comment.class);
//        //                Prefrences.Companion.saveBoolean(LoginActivity.this,KEY_IS_LOGIN,true);
//        //                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
//        //                // ...
//        //            }
//        //
//        //            @Override
//        //            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//        //                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
//        //
//        //                // A comment has changed, use the key to determine if we are displaying this
//        //                // comment and if so displayed the changed comment.
//        //                Comment newComment = dataSnapshot.getValue(Comment.class);
//        //                String commentKey = dataSnapshot.getKey();
//        //
//        //                // ...
//        //            }
//        //
//        //            @Override
//        //            public void onChildRemoved(DataSnapshot dataSnapshot) {
//        //                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
//        //
//        //                // A comment has changed, use the key to determine if we are displaying this
//        //                // comment and if so remove it.
//        //                String commentKey = dataSnapshot.getKey();
//        //
//        //                // ...
//        //            }
//        //
//        //            @Override
//        //            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//        //                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
//        //
//        //                // A comment has changed position, use the key to determine if we are
//        //                // displaying this comment and if so move it.
//        //                Comment movedComment = dataSnapshot.getValue(Comment.class);
//        //                String commentKey = dataSnapshot.getKey();
//        //
//        //                // ...
//        //            }
//        //
//        //            @Override
//        //            public void onCancelled(DatabaseError databaseError) {
//        //                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
//        //                Toast.makeText(LoginActivity.this, "Failed to load comments.",
//        //                        Toast.LENGTH_SHORT).show();
//        //            }
//        //        };
//        //        mUserDatabase.addChildEventListener(childEventListener);
//
//
//    }
}
