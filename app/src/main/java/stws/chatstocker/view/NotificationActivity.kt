package stws.chatstocker.view

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_notification.*
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityFullscreenImageBinding
import stws.chatstocker.databinding.ActivityNotificationBinding

class NotificationActivity: AppCompatActivity(),View.OnClickListener {

    lateinit var vibrate: RadioButton
    lateinit var silent: RadioButton
    lateinit var tone: RadioButton
    private lateinit var activityFullscreenImageBinding: ActivityNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFullscreenImageBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification)


    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.vibrateRadio->{
vibrate.isChecked=true;
            }
            R.id.silentRadio->{

            }
            R.id.toneRadio->{

            }
        }
    }
}