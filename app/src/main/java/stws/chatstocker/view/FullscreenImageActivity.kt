package stws.chatstocker.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityFullscreenImageBinding

class FullscreenImageActivity : AppCompatActivity() {
    private lateinit var activityFullscreenImageBinding: ActivityFullscreenImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFullscreenImageBinding = DataBindingUtil.setContentView(this, R.layout.activity_fullscreen_image)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar()!!.hide();
        val imageView = activityFullscreenImageBinding.imageView;
        imageView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.setAdjustViewBounds(false);
        Glide.with(this).load(intent.getStringExtra(ConstantsValues.KEY_FILE_URL)).into(imageView)
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }
}
