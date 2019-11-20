package stws.chatstocker.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityFullscreenImageBinding
import stws.chatstocker.model.FileDetails
import stws.chatstocker.viewmodel.PhotoViewModel

class FullscreenImageActivity : AppCompatActivity() {
    private lateinit var activityFullscreenImageBinding: ActivityFullscreenImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFullscreenImageBinding = DataBindingUtil.setContentView(this, R.layout.activity_fullscreen_image)
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val viewModel=ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        val photos=intent.getParcelableExtra<FileDetails>(ConstantsValues.KEY_FILE)
        viewModel.photos=photos

        viewModel.isVideo=false
        viewModel.context=this
        viewModel.drive=BaseActivity.mDriveService
        activityFullscreenImageBinding.viewModel=viewModel
//        getSupportActionBar()!!.hide();
        val imageView = activityFullscreenImageBinding.imageView;
        imageView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        imageView.setAdjustViewBounds(false);
        Glide.with(this).load(intent.getStringExtra(ConstantsValues.KEY_FILE_URL)).into(imageView)
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home)
            super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        Log.e("onstart","true")
    }

    override fun onPause() {
        super.onPause()
        Log.e("onpause","true")
    }

    override fun onStop() {
        super.onStop()
        Log.e("onstop","true")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("ondes","true")
    }

    override fun onResume() {
        super.onResume()
        Log.e("onres","true")
    }

    override fun onRestart() {
        super.onRestart()
        Log.e("onrest","true")
    }
}
