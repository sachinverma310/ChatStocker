package stws.chatstocker.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityAccountDetailBinding
import stws.chatstocker.viewmodel.AccountDetailsViewModel


class AccountActvity: AppCompatActivity() {
    private lateinit var activityFullscreenImageBinding: ActivityAccountDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFullscreenImageBinding = DataBindingUtil.setContentView(this, R.layout.activity_account_detail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val viewModel=ViewModelProviders.of(this).get(AccountDetailsViewModel::class.java)
        activityFullscreenImageBinding.viewModel=viewModel
        setTitle("Account Settings")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home)
            super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    fun accountInfo(view:View){
       // startActivity(Intent(this, AccountActvity::class.java))

    }
    fun deleteAccount(view: View){
        //startActivity(Intent(this, AccountActvity::class.java))
    }
    fun appinformation(view: View){
        val intent=Intent(this, AppInfromation::class.java)
        intent.putExtra(ConstantsValues.KEY_FROM,"appinfo")
        startActivity(intent)
      //  startActivity(Intent(this, AccountActvity::class.java))
    }
}