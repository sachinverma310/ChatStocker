package stws.chatstocker.view.adapter

import android.content.Context
import android.os.Environment
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityFullscreenImageBinding
import stws.chatstocker.model.FileDetails
import stws.chatstocker.model.ImageList
import stws.chatstocker.view.BaseActivity
import stws.chatstocker.viewmodel.PhotoViewModel
import java.io.File

class SlidingImage_Adapter(private val context: Context, private val imageModelArrayList: List<FileDetails>) : PagerAdapter() {
    private val inflater: LayoutInflater
    lateinit var binding:ActivityFullscreenImageBinding

    val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Chatstocker")
    init {
        inflater = LayoutInflater.from(context)

    }
 
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
 
    override fun getCount(): Int {
        return imageModelArrayList.size
    }
 
    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        binding=DataBindingUtil.inflate(inflater,R.layout.activity_fullscreen_image, view, false)
//        val imageLayout = inflater.inflate(R.layout.activity_fullscreen_image, view, false)!!
        var photoFile = File(file.path + File.separator
                + imageModelArrayList.get(position).fileId + "." + "jpg")
        val imageView = binding.imageView
//        var viewModel:PhotoViewModel
//        if (binding.viewModel==null)
        val viewModel= PhotoViewModel()

        viewModel.photos=imageModelArrayList.get(position)

        viewModel.isVideo=false
        viewModel.context=context
        viewModel.drive= BaseActivity.mDriveService
        Glide.with(context).load(photoFile).into(imageView)
        binding.viewModel=viewModel
//        imageView.setImageResource(imageModelArrayList[position].getImage_drawables())
 
        view.addView(binding.root, 0)
 
        return binding.root
    }
 
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
 
    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}
 
    override fun saveState(): Parcelable? {
        return null
    }
 
}