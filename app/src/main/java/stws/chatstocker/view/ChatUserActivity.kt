package stws.chatstocker.view

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

import com.google.firebase.database.ServerValue
import stws.chatstocker.R


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
    override fun onStop() {
        super.onStop()

       //-----for disabling online function when appliction runs in background----
        val user=mauth!!.getCurrentUser();
        if(user!=null){
            mDatabaseReference!!.child(user.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
        }

    }
}
