package stws.chatstocker.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.gms.tasks.OnSuccessListener
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.utils.Prefrences

class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setTitle("Settings")

    }
    fun logout(view: View){
        BaseActivity.client.signOut().addOnSuccessListener(OnSuccessListener<Void> {
            Prefrences.saveBoolean(this, ConstantsValues.KEY_IS_LOGIN,false)
            Prefrences.saveInt(this,ConstantsValues.KEY_Noti_RADIO_ID,0)
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        })
    }
    fun account(view: View){
//        client.signOut().addOnSuccessListener(OnSuccessListener<Void> {
            startActivity(Intent(this, AccountActvity::class.java))
//            finishAffinity()

    }
    fun notification(view: View){
        startActivity(Intent(this, NotificationActivity::class.java))
//        finishAffinity()
    }
    fun help(view: View){
        startActivity(Intent(this, HelpActivity::class.java))
//        finishAffinity()
    }
}
