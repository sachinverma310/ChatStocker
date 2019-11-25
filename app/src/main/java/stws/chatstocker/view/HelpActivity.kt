package stws.chatstocker.view

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityFullscreenImageBinding
import stws.chatstocker.databinding.ActivityHelpBinding
import stws.chatstocker.view.fragments.ContactActivity
import stws.chatstocker.view.fragments.ContactsFragment

class HelpActivity :  AppCompatActivity() {
    private lateinit var activityFullscreenImageBinding: ActivityHelpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFullscreenImageBinding = DataBindingUtil.setContentView(this, R.layout.activity_help)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setTitle("Help")
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home)
            super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
    fun contactus(view: View){
        startActivity(Intent(this, ContactActivity::class.java))

    }

    fun privacyPolicy(view: View){
        val intent=Intent(this, AppInfromation::class.java)
        intent.putExtra(ConstantsValues.KEY_FROM,"privacypolicy")
        startActivity(intent)

    }

    fun faqs(view: View){
        val intent=Intent(this, AppInfromation::class.java)
        intent.putExtra(ConstantsValues.KEY_FROM,"faqs")
        startActivity(intent)

    }
    fun termsNcondition(view: View){
        val intent=Intent(this, AppInfromation::class.java)
        intent.putExtra(ConstantsValues.KEY_FROM,"tc")
        startActivity(intent)

    }


    }