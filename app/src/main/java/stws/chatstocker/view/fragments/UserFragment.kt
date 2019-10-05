package stws.chatstocker.view.fragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.user_action_bar.*
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityUserBinding
import stws.chatstocker.model.User
import stws.chatstocker.view.BaseActivity
import stws.chatstocker.view.adapter.UserAdapter
import stws.chatstocker.viewmodel.UserListViewModel

class UserFragment : BaseActivity() {
    private lateinit var actviUserBinding:ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actviUserBinding=DataBindingUtil.inflate(layoutInflater,R.layout.activity_user,frameLayout,true)
        userActionBar.visibility=View.VISIBLE
        mainActionBar.visibility=View.GONE
        val viewModel=ViewModelProviders.of(this).get(UserListViewModel::class.java)
        val recyclerView=actviUserBinding.recyclerView;
        recyclerView.itemAnimator= DefaultItemAnimator()
        recyclerView.layoutManager= LinearLayoutManager(this)
        viewModel.userList(this)!!.observe(this, Observer <ArrayList<User>>{users->
            recyclerView.adapter= UserAdapter(this@UserFragment,users)
        })



    }

}
