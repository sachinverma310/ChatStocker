package stws.chatstocker.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager

import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityImageViewBinding
import stws.chatstocker.model.FileDetails
import stws.chatstocker.model.ImageList
import stws.chatstocker.utils.FileSingleton
import stws.chatstocker.view.adapter.SlidingImage_Adapter
import stws.chatstocker.viewmodel.PhotoViewModel
import java.io.File



class ImageViewActivity : AppCompatActivity() {
    internal lateinit var viewpager: ViewPager
    lateinit var binding: ActivityImageViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_view)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)



//        binding.viewModel=viewModel
        viewpager =binding.viewpager
        getImages()
//        val imageList = ImageList(photos, intent.getStringExtra(ConstantsValues.KEY_FILE_URL))
//        val adapter = SlidingImage_Adapter(this)
//        viewpager.adapter = adapter
    }

    fun getImages() {
        val photos = intent.getParcelableExtra<FileDetails>(ConstantsValues.KEY_FILE)
        val fileList= FileSingleton.getInstance().list

        val pos=fileList!!.indexOf(photos)
        val adapter = SlidingImage_Adapter(this,fileList!!)
        viewpager.adapter = adapter
        viewpager.setCurrentItem(pos)

    }





}

