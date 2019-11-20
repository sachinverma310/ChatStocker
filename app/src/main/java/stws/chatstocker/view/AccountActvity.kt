package stws.chatstocker.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityAccountDetailBinding


class AccountActvity: AppCompatActivity() {
    private lateinit var activityFullscreenImageBinding: ActivityAccountDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFullscreenImageBinding = DataBindingUtil.setContentView(this, R.layout.activity_account_detail)
    }
}