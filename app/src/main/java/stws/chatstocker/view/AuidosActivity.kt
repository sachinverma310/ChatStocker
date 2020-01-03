package stws.chatstocker.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_layout.*
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityAuidosBinding
import stws.chatstocker.databinding.ActivityPhotosBinding
import stws.chatstocker.interfaces.FileRecievedListener
import stws.chatstocker.model.FileDetails
import stws.chatstocker.utils.*
import stws.chatstocker.view.BaseActivity.mDriveService
import stws.chatstocker.view.adapter.AudioAdapter
import stws.chatstocker.view.adapter.PhotoAdapter
import stws.chatstocker.view.adapter.SectionedGridRecyclerViewAdapter
import stws.chatstocker.viewmodel.AudioViewModel
import stws.chatstocker.viewmodel.PhotoViewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AuidosActivity : AppCompatActivity(), GetAllFiles.OnFileReciveListener, FileRecievedListener, AudioAdapter.FileSelectedListener {
    override fun selectedFile(photos: ArrayList<FileDetails>) {
        if (photos.size > 0)
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        else
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
        photoViewModel.list = photos;
    }

    lateinit var fileList: MutableList<FileDetails>
    var hashMapFileList: HashMap<String, Int> = HashMap()
    lateinit var adapter: AudioAdapter
    override fun Downloaded(list: List<FileDetails>) {
//        Glide.with(this).load(list.get(0)).into(btn)
//        fileList.addAll(list)
//        Collections.sort(fileList, SortByTime());
//        ProgressBarHandler.hide()
//        var createdtime = ""
//        for (i in 0 until fileList.size) {
//            createdtime = DateTimeUtils.convertDateTimetoDay(fileList.get(i).createdTime, "dd MMM")
//            if (hashMapFileList.containsKey(createdtime)) {
//                hashMapFileList.put(createdtime, hashMapFileList.get(createdtime)!!.plus(1))
//            } else {
//                hashMapFileList.put(createdtime, 1)
//            }
//        }
//        var layoutManager = GridLayoutManager(this, 3)
//        recyclerView.layoutManager = layoutManager!!
//        val sections = ArrayList<SectionedGridRecyclerViewAdapter.Section>()
//        var prevValue = 0
//        //Sections
//        var count = 0;
//        val result = hashMapFileList.toSortedMap(compareByDescending { it })
//        result.forEach { (key, value) ->
//            if (count == 0)
//                sections.add(SectionedGridRecyclerViewAdapter.Section(0, key))
//            else
//                sections.add(SectionedGridRecyclerViewAdapter.Section(prevValue, key))
//            prevValue = value + prevValue
//            count++;
//
//        }
//
//
//        //Add your adapter to the sectionAdapter
//        adapter = AudioAdapter(fileList, BaseActivity.mDriveService, this)
//
//        val mSectionedAdapter = SectionedGridRecyclerViewAdapter(this, R.layout.section, R.id.section_text, recyclerView, adapter)
//        mSectionedAdapter.setSections(sections.toTypedArray<SectionedGridRecyclerViewAdapter.Section>())
//
//        //Apply this adapter to the RecyclerView
//        recyclerView.setAdapter(mSectionedAdapter)
//        ProgressBarHandler.hide()
        fileList.addAll(list)
        Collections.sort(fileList, SortByTime());
        ProgressBarHandler.hide()
        var createdtime = ""
        for (i in 0 until fileList.size) {
            createdtime = DateTimeUtils.convertDateTimetoDay(fileList.get(i).createdTime, "dd MMM")
            val millisecond=DateTimeUtils.convertStringtoMillis(createdtime,"dd MMM")
            if (hashMapFileList.containsKey(millisecond.toString())) {
                hashMapFileList.put(millisecond.toString(), hashMapFileList.get(millisecond.toString())!!.plus(1))
            } else {
                hashMapFileList.put(millisecond.toString(), 1)
            }
        }
        var layoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = layoutManager!!
        val sections = ArrayList<SectionedGridRecyclerViewAdapter.Section>()
        var prevValue = 0
        //Sections
        var count = 0;
        val results = TreeMap<String,Int>()
        results.putAll(hashMapFileList)
        val result=results.descendingMap()
        result.forEach { (key, value) ->
            if (count == 0)
                sections.add(SectionedGridRecyclerViewAdapter.Section(0, DateTimeUtils.convertDateTimetoDay(key.toLong(), "dd MMM")))
            else
                sections.add(SectionedGridRecyclerViewAdapter.Section(prevValue, DateTimeUtils.convertDateTimetoDay(key.toLong(), "dd MMM")))
            prevValue = value + prevValue
            count++;

        }
//        Glide.with(this).load(list.get(0)).into(btn)
//        ProgressBarHandler.hide()
//        Collections.sort(fileList)
        val adapter = AudioAdapter(fileList, BaseActivity.mDriveService, this)
        recyclerView.adapter = adapter
        val mSectionedAdapter = SectionedGridRecyclerViewAdapter(this, stws.chatstocker.R.layout.section, stws.chatstocker.R.id.section_text, recyclerView, adapter)
        mSectionedAdapter.setSections(sections.toTypedArray<SectionedGridRecyclerViewAdapter.Section>())

        //Apply this adapter to the RecyclerView
        recyclerView.setAdapter(mSectionedAdapter)
    }

    override fun onFileRecive(id: String) {
        ProgressBarHandler.hide()
        val isfromCurrent = intent.getBooleanExtra(ConstantsValues.KEY_ISFROM_CURRENT, false)
        DriveServiceHelper.getInstance(mDriveService).GetFilesUrl(this, id, this, isfromCurrent).execute()
    }

    lateinit var photosBinding: ActivityAuidosBinding
    lateinit var btn: ImageView
    lateinit var recyclerView: RecyclerView
    lateinit var photoViewModel: AudioViewModel
    private lateinit var sheetBehavior: BottomSheetBehavior<ConstraintLayout>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photosBinding = DataBindingUtil.setContentView(this, R.layout.activity_auidos)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        setContentView(R.layout.activity_audio_recording)
        setTitle(resources.getString(R.string.current_years_audio))
        val isfromCurrent = intent.getBooleanExtra(ConstantsValues.KEY_ISFROM_CURRENT, false)
        if (!isfromCurrent)
            setTitle(resources.getString(R.string.prev_years_audio))
        recyclerView = photosBinding.recyclerView
//        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.itemAnimator = DefaultItemAnimator()
//        ProgressBarHandler.getInstance()
//        ProgressBarHandler.show(this)
        photoViewModel = ViewModelProviders.of(this).get(AudioViewModel::class.java)
        photosBinding.viewModel = photoViewModel
        photoViewModel.drive = BaseActivity.mDriveService

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

        callAudioListApi()

    }

    private fun callAudioListApi() {
        fileList = ArrayList<FileDetails>()
        hashMapFileList.clear()
        fileList.clear()
        ProgressBarHandler.show(this)
        GetAllFiles(this, "Chat Stocker audio", BaseActivity.mDriveServiceHelper, BaseActivity.mDriveService, this@AuidosActivity, "audio/mpeg").execute()
    }

    override fun onResume() {
        super.onResume()


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            super.onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ConstantsValues.KEY_FULL_SCREEN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val photos = intent.getParcelableExtra<FileDetails>(ConstantsValues.KEY_FILE)
//                val fileDetails=intent.getParcelableExtra<FileDetails>(ConstantsValues.KEY_FILE)
                callAudioListApi()
            }
        }
    }
}

