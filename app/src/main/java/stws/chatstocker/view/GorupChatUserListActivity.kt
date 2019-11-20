package stws.chatstocker.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.group_action_bar.view.*
import kotlinx.android.synthetic.main.search_bar_layout.*
import kotlinx.android.synthetic.main.search_bar_layout.view.*
import kotlinx.android.synthetic.main.user_action_bar.*
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityGorupChatBinding
import stws.chatstocker.model.GroupUser
import stws.chatstocker.model.GroupUserList
import stws.chatstocker.model.LoginResponse
import stws.chatstocker.model.User
import stws.chatstocker.utils.Prefrences
import stws.chatstocker.view.adapter.GroupUserAdapter
import stws.chatstocker.view.adapter.UserAdapter
import stws.chatstocker.view.adapter.UserHorizontalAdapter
import stws.chatstocker.viewmodel.UserListViewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GorupChatUserListActivity : AppCompatActivity(), GroupUserAdapter.ItemSelectedListener,ConstantsValues {

    override fun onItemUnSelected(position: Int) {

//        for (i in 0 until userList.size){
//            if (userList.get(i).uid==userListSelected.get(position).uid)
//                index=Integer(i);
//        }
        val index= userListSelected.get(position).pos
        val groupUserList:GroupUserList= GroupUserList(userListSelected.get(position).pos,userListSelected.get(position).pos,userList.get(userListSelected.get(position).pos))
        userListSelected.remove(groupUserList)
        userList.get(index).isSeletced = false
        userAdapter.notifyDataSetChanged()
        if (userListSelected.size == 0)
            recyclerViewSelected.visibility = View.GONE
        tvTotalPrtcpnt.setText(userListSelected.size.toString()+" Participent")
    }

    override fun onItemSelected(position: Int, isRemoved: Boolean) {
        val groupUserList:GroupUserList= GroupUserList(position,position,userList.get(position))
        if (isRemoved) {
//            = ArrayList()
//            userListSelected.remove(userList.get(position))
            userListSelected.remove(groupUserList)
            selectedUserAdapter!!.removeUser(groupUserList.user!!)
            tvTotalPrtcpnt.setText(userListSelected.size.toString()+" Participent")
//            selectedUserAdapter.notifyDataSetChanged()

        } else {
            userListSelected.add(groupUserList)
//            if (position==0) {
            if (selectedUserAdapter==null){
                addSelectedUser(0)
                selectedUserAdapter!!.addUser(groupUserList.user!!)
            }
            else
                selectedUserAdapter!!.addUser(groupUserList.user!!)
            tvTotalPrtcpnt.setText(userListSelected.size.toString()+" Participent")
//            selectedUserAdapter.notifyDataSetChanged()

        }
        if (userListSelected.size > 0)
            recyclerViewSelected.visibility = View.VISIBLE
    }

    private lateinit var userList: ArrayList<User>
    private lateinit var index: Integer
    private lateinit var userListSelected: ArrayList<GroupUserList>
    private lateinit var userAdapter: GroupUserAdapter
    private  var selectedUserAdapter: UserHorizontalAdapter?=null
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var loginResponse: LoginResponse
    private lateinit var imgSearch: ImageView
    private lateinit var recyclerViewSelected: RecyclerView
    private lateinit var tvTotalPrtcpnt:TextView
    private lateinit var fbNext:FloatingActionButton
    private lateinit var userSelectedList:ArrayList<User>
    private  var    grpUserList:ArrayList<User>?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityGrChaBinding = DataBindingUtil.setContentView<ActivityGorupChatBinding>(this, R.layout.activity_gorup_chat)
        val recyclerView = activityGrChaBinding.recyclerView;
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerViewSelected = activityGrChaBinding.recyclerViewHorz;
        recyclerViewSelected.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imgSearch = activityGrChaBinding.groupActvionBar.imgSearch
        tvTotalPrtcpnt=activityGrChaBinding.groupActvionBar.tvTotalParticipent
        fbNext=activityGrChaBinding.fbNext
        tvTotalPrtcpnt.setText("0 Participants")
        userList = ArrayList()
        userListSelected = ArrayList()
        userSelectedList= ArrayList()
        grpUserList=intent.getParcelableArrayListExtra<User>(ConstantsValues.KEY_GROUP_DETAILS)
        val viewModel = ViewModelProviders.of(this).get(UserListViewModel::class.java)
        viewModel.userList(this)!!.observe(this, Observer {
            userList.addAll(it)

            userAdapter = GroupUserAdapter(this, userList, this)
            recyclerView.adapter = userAdapter


            if (grpUserList!=null){
                for (i in 0 until grpUserList!!.size ) {
                    for (  j in 0 until userList.size)
                        if (userList.get(j).uid.equals(grpUserList!!.get(i).uid)) {
                            userList.removeAt(j)
                          break
                        }
                    userAdapter.notifyDataSetChanged()
                }

            }
        })

        imgSearch.setOnClickListener(View.OnClickListener {
            activityGrChaBinding.groupActvionBar.searchBarLayout.visibility = View.VISIBLE

        })
        activityGrChaBinding.groupActvionBar.searchBarLayout.imgCancel.setOnClickListener(View.OnClickListener {
            activityGrChaBinding.groupActvionBar.searchBarLayout.visibility = View.GONE
        })
//        imgSeacrhName.setOnClickListener(View.OnClickListener {
//        })
//        imgMore.setOnClickListener(View.OnClickListener {
////            showPopup(imgMore)
//        })
        activityGrChaBinding.groupActvionBar.searchBarLayout.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.length > 0)
                    filter(s.toString())
                else
                    userAdapter.updateList(userList);
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        loginResponse = Prefrences.getUserDetails(this, ConstantsValues.KEY_LOGIN_DATA)

        fbNext.setOnClickListener(View.OnClickListener {
            if (grpUserList==null) {
                val intent = Intent(this, CreateGroupActivity::class.java)
                intent.putExtra(ConstantsValues.KEY_GRP_USER, userListSelected)
                startActivity(intent)
            }
            else{
                val intent = Intent()
                intent.putExtra(ConstantsValues.KEY_GRP_USER, userListSelected)

                setResult(Activity.RESULT_OK,intent)
                finish()
            }
        })
    }

    fun addSelectedUser(pos:Int) {
        selectedUserAdapter = UserHorizontalAdapter(this,userListSelected.get(pos).user!! , this)
        recyclerViewSelected.adapter = selectedUserAdapter
    }

    fun filter(text: String) {
        val temp = ArrayList<User>();
        for (d: User in userList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.name!!.toLowerCase()!!.contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        //update recyclerview
        userAdapter.updateList(temp);
    }

    override fun onStart() {
        super.onStart()
//        val user = mauth!!.getCurrentUser()
//        if (user == null) {
////            startfn()
//        } else {
        //---IF LOGIN , ADD ONLINE VALUE TO TRUE---
        mDatabaseReference!!.child(loginResponse.uid!!).child("online").setValue(true)

//        }
    }

    override fun onDestroy() {
//        val user=mauth!!.getCurrentUser();
//        if(user!=null){
        mDatabaseReference!!.child(loginResponse.uid!!).child("lastSeen").setValue(Calendar.getInstance().timeInMillis.toString());
//        }
        mDatabaseReference!!.child(loginResponse.uid!!).child("online").setValue(false);
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()

        //-----for disabling online function when appliction runs in background----
//        val user=mauth!!.getCurrentUser();
//        if(user!=null){
//            mDatabaseReference!!.child(loginResponse.uid!!).child("online").setValue(false);
//        }

    }


}
