package stws.chatstocker.view.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.search_bar_layout.*
import kotlinx.android.synthetic.main.user_action_bar.*
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityUserBinding
import stws.chatstocker.model.ChatMessage
import stws.chatstocker.model.FileDetails
import stws.chatstocker.model.LoginResponse
import stws.chatstocker.model.User
import stws.chatstocker.utils.Prefrences
import stws.chatstocker.view.BaseActivity
import stws.chatstocker.view.GorupChatUserListActivity
import stws.chatstocker.view.adapter.UserAdapter
import stws.chatstocker.viewmodel.UserListViewModel
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class UserFragment : BaseActivity() {
    private lateinit var actviUserBinding: ActivityUserBinding
    private lateinit var userList: ArrayList<User>
    private lateinit var userAdapter: UserAdapter
    var mDatabaseReference: DatabaseReference? = null
    private val mauth: FirebaseAuth? = null
    private lateinit var loginResponse: LoginResponse
    private var fileUrl: ArrayList<File>? = null
    private var urlList: ArrayList<ChatMessage>? = null
    private lateinit var mSwipeRefreshLayout:SwipeRefreshLayout
    private lateinit var viewModel:UserListViewModel
    private lateinit var  recyclerView:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actviUserBinding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_user, frameLayout, true)
        userActionBar.visibility = View.VISIBLE
        mainActionBar.visibility = View.GONE
        userList = ArrayList()
        mSwipeRefreshLayout=actviUserBinding.swipeContainer
        mSwipeRefreshLayout.setRefreshing(true);
        tvTitle.setText("Chat")
         viewModel = ViewModelProviders.of(this).get(UserListViewModel::class.java)
         recyclerView = actviUserBinding.recyclerView;
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.layoutManager = LinearLayoutManager(this)
        if (intent.getSerializableExtra(ConstantsValues.KEY_FILE_LIST) != null)
            fileUrl = intent.getSerializableExtra(ConstantsValues.KEY_FILE_LIST) as ArrayList<File>?
        else if (intent.getSerializableExtra(ConstantsValues.KEY_URL_LIST) != null)
            urlList = intent.getParcelableArrayListExtra<ChatMessage>(ConstantsValues.KEY_URL_LIST)
        userAdapter = UserAdapter(this@UserFragment, userList)

        mSwipeRefreshLayout.setOnRefreshListener(object :SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                userList.clear()
             refreshUserList()

            }

        })
        viewModel.getGroupList(this)
        imgSearchBar.setOnClickListener(View.OnClickListener {
            actionSearchBar.visibility = View.VISIBLE
            userActionBar.visibility = View.GONE
        })
        imgCancel.setOnClickListener(View.OnClickListener {
            actionSearchBar.visibility = View.GONE
            userActionBar.visibility = View.VISIBLE
        })
        imgSeacrhName.setOnClickListener(View.OnClickListener {
        })
        imgMore.setOnClickListener(View.OnClickListener {
            showPopup(imgMore)
        })
        edtSearch.addTextChangedListener(object : TextWatcher {
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
    }

    fun filter(text: String) {
        val temp = ArrayList<User>();
        for (d: User in userList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.name!!.contains(text)) {
                temp.add(d);
            }
        }
        //update recyclerview
        userAdapter.updateList(temp);
    }
    fun refreshUserList(){
        viewModel.userList(this,false)!!.observe(this, Observer<ArrayList<User>> { users ->
            mSwipeRefreshLayout.setRefreshing(false);
            userList.addAll(users)

            if (fileUrl != null)
                userAdapter.setExternalFileUrl(fileUrl!!)
            else if (urlList != null)
                userAdapter.setForwardingUrl(urlList!!)
            recyclerView.adapter = userAdapter
        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent!!.getSerializableExtra(ConstantsValues.KEY_URL_LIST) != null) {
            urlList = intent.getParcelableArrayListExtra<ChatMessage>(ConstantsValues.KEY_URL_LIST)
            userAdapter.setForwardingUrl(urlList!!)
        }
        userAdapter.notifyDataSetChanged()
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

    override fun onResume() {
        super.onResume()
        userList.clear()
        mSwipeRefreshLayout.setRefreshing(true);
        viewModel.userList(this,false)!!.observe(this, Observer<ArrayList<User>> { users ->
            refreshUserList()
        })
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

    fun showPopup(view: ImageView) {
        val popup = PopupMenu(view.context, view);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.user_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                when (item!!.itemId) {
                    R.id.action_create_group -> {
                        startActivity(Intent(this@UserFragment, GorupChatUserListActivity::class.java))
                    }

                }
                return true
            }

        });

        popup.show();//showing popup menu
    }
}
