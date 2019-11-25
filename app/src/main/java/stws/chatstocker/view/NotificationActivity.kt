package stws.chatstocker.view

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_notification.*
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityFullscreenImageBinding
import stws.chatstocker.databinding.ActivityNotificationBinding
import stws.chatstocker.utils.Prefrences

class NotificationActivity : AppCompatActivity() {

    lateinit var vibrate: RadioButton
    lateinit var silent: RadioButton
    lateinit var rbGrp: RadioGroup
    private lateinit var activityFullscreenImageBinding: ActivityNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFullscreenImageBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setTitle("Notification Settings")
        rbGrp=activityFullscreenImageBinding.rbgrp
        if (Prefrences.getIntValue(this@NotificationActivity,ConstantsValues.KEY_Noti_RADIO_ID)!=-1) {
            val radioButton=rbGrp.findViewById<RadioButton>(Prefrences.getIntValue(this@NotificationActivity,ConstantsValues.KEY_Noti_RADIO_ID)!!)
            radioButton.isChecked=true;
        }
        rbGrp.setOnCheckedChangeListener(object :RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                val radioButton=group!!.findViewById<RadioButton>(checkedId)
                Prefrences.saveInt(this@NotificationActivity,ConstantsValues.KEY_Noti_RADIO_ID,checkedId)
                Prefrences.saveString(this@NotificationActivity,ConstantsValues.KEY_NotI_SETTING,radioButton.text.toString())
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home)
            super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }


}