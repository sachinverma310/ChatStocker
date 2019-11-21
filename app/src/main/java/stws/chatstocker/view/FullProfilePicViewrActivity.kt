package stws.chatstocker.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityFullProfilePicViewrBinding
import stws.chatstocker.databinding.ActivityFullscreenImageBinding
import stws.chatstocker.model.FileDetails
import stws.chatstocker.viewmodel.PhotoViewModel

class FullProfilePicViewrActivity : AppCompatActivity() {

    private lateinit var activityFullscreenImageBinding: ActivityFullProfilePicViewrBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFullscreenImageBinding = DataBindingUtil.setContentView(this, R.layout.activity_full_profile_pic_viewr)
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        val title=intent.getStringExtra(ConstantsValues.KEY_USER_ID)
        setTitle(title)
//        getSupportActionBar()!!.hide();
        val imageView = activityFullscreenImageBinding.imageView;
//        imageView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
//        imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
//        imageView.setAdjustViewBounds(false);
        Glide.with(this).load(intent.getStringExtra(ConstantsValues.KEY_FILE_URL)).into(imageView)
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home)
            super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
