package stws.chatstocker.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityFullscreenImageBinding
import stws.chatstocker.databinding.ActivityHelpBinding
import stws.chatstocker.view.fragments.ContactsFragment

class HelpActivity :  AppCompatActivity() {
    private lateinit var activityFullscreenImageBinding: ActivityHelpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFullscreenImageBinding = DataBindingUtil.setContentView(this, R.layout.activity_help)
    }
    fun contactus(view: View){
        startActivity(Intent(this, ContactsFragment::class.java))
        finishAffinity()
    }

    fun privacyPolicy(view: View){
        startActivity(Intent(this, ContactsFragment::class.java))
        finishAffinity()
    }

    fun faqs(view: View){
        startActivity(Intent(this, ContactsFragment::class.java))
        finishAffinity()
    }
    fun termsNcondition(view: View){
        startActivity(Intent(this, ContactsFragment::class.java))
        finishAffinity()
    }


    }