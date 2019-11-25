package stws.chatstocker.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.api.services.drive.model.FileList
import kotlinx.android.synthetic.main.bottom_sheet_layout.*
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityPhotosBinding
import stws.chatstocker.interfaces.FileRecievedListener
import stws.chatstocker.model.FileDetails
import stws.chatstocker.utils.*
import stws.chatstocker.view.adapter.PhotoAdapter
import stws.chatstocker.view.adapter.SectionedGridRecyclerViewAdapter
import stws.chatstocker.viewmodel.PhotoViewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class VideoSActivity : AppCompatActivity() , GetAllFiles.OnFileReciveListener, FileRecievedListener,PhotoAdapter.FileSelectedListener {
    override fun selectedFile(photos: ArrayList<FileDetails>) {
        Log.e("list", photos.toString())
        if (photos.size > 0)
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        else
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        photoViewModel.list=photos;
    }

    lateinit var fileList: MutableList<FileDetails>
    var hashMapFileList: HashMap<String, Int> = HashMap()
    lateinit var photoViewModel: PhotoViewModel
    override fun Downloaded(list: List<FileDetails>) {
        fileList.addAll(list)
        Collections.sort(fileList, SortByTime());
        ProgressBarHandler.hide()
        var createdtime = ""
        for (i in 0 until fileList.size) {
            createdtime = DateTimeUtils.convertDateTimetoDay(fileList.get(i).createdTime, "dd MMM")
            if (hashMapFileList.containsKey(createdtime)) {
                hashMapFileList.put(createdtime, hashMapFileList.get(createdtime)!!.plus(1))
            } else {
                hashMapFileList.put(createdtime, 1)
            }
        }
        var layoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = layoutManager!!
        val sections = ArrayList<SectionedGridRecyclerViewAdapter.Section>()
        var prevValue = 0
        //Sections
        var count = 0;
        val result =   hashMapFileList.toSortedMap(compareByDescending { it })
        result.forEach { (key, value) ->
            if (count == 0)
                sections.add(SectionedGridRecyclerViewAdapter.Section(0, key))
            else
                sections.add(SectionedGridRecyclerViewAdapter.Section(prevValue, key))
            prevValue = value + prevValue
            count++;

        }
//        Glide.with(this).load(list.get(0)).into(btn)
//        ProgressBarHandler.hide()
        val adapter=PhotoAdapter(this@VideoSActivity,BaseActivity.mDriveService,fileList,true,this)
        recyclerView.adapter = adapter
        val mSectionedAdapter = SectionedGridRecyclerViewAdapter(this, stws.chatstocker.R.layout.section, stws.chatstocker.R.id.section_text, recyclerView, adapter)
        mSectionedAdapter.setSections(sections.toTypedArray<SectionedGridRecyclerViewAdapter.Section>())

        //Apply this adapter to the RecyclerView
        recyclerView.setAdapter(mSectionedAdapter)
    }

    override fun onFileRecive(id: String) {
        ProgressBarHandler.hide()
        val isfromCurrent=intent.getBooleanExtra(ConstantsValues.KEY_ISFROM_CURRENT,false)
        DriveServiceHelper.getInstance(BaseActivity.mDriveService).GetFilesUrl(this,id, this,isfromCurrent).execute()
    }

    lateinit var photosBinding: ActivityPhotosBinding
    lateinit var btn: ImageView
    lateinit var recyclerView: RecyclerView
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photosBinding = DataBindingUtil.setContentView(this, R.layout.activity_photos)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        setContentView(R.layout.activity_audio_recording)
        setTitle(resources.getString(R.string.current_years_videos))
        val isfromCurrent=intent.getBooleanExtra(ConstantsValues.KEY_ISFROM_CURRENT,false)
        if (!isfromCurrent)
            setTitle(resources.getString(R.string.prev_years_videos))
        recyclerView = photosBinding.recyclerView
//        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.itemAnimator = DefaultItemAnimator()
        photoViewModel = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        photosBinding.viewModel=photoViewModel
        photoViewModel.isVideo=true
        photoViewModel.drive=BaseActivity.mDriveService
//        val photoViewModel=ViewModelProviders.of(this).get(PhotoViewModel::class.java)
      /*  ProgressBarHandler.getInstance()
        ProgressBarHandler.show(this)*/
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
//        expandCloseSheet()
        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                    }
//                        btBottomSheet.text = "Close Bottom Sheet"
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                    }
//                        btBottomSheet.text = "Expand Bottom Sheet"
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })
        getVideosApi()


    }
    private fun getVideosApi(){
       fileList = ArrayList<FileDetails>()
        fileList.clear()
        hashMapFileList.clear()
        ProgressBarHandler.show(this)
        GetAllFiles(this, "Chat Stocker videos", BaseActivity.mDriveServiceHelper, BaseActivity.mDriveService, this@VideoSActivity,"video/mp4").execute()
    }

    override fun onResume() {
        super.onResume()


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
            super.onBackPressed()
        return true
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ConstantsValues.KEY_FULL_SCREEN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val photos = intent.getParcelableExtra<FileDetails>(ConstantsValues.KEY_FILE)
//                val fileDetails=intent.getParcelableExtra<FileDetails>(ConstantsValues.KEY_FILE)
                getVideosApi()
            }
        }
    }
}
