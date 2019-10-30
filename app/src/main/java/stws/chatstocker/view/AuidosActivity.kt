package stws.chatstocker.view

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityPhotosBinding
import stws.chatstocker.interfaces.FileRecievedListener
import stws.chatstocker.model.FileDetails
import stws.chatstocker.utils.DriveServiceHelper
import stws.chatstocker.utils.GetAllFiles
import stws.chatstocker.view.BaseActivity.mDriveService
import stws.chatstocker.view.adapter.AudioAdapter
import stws.chatstocker.view.adapter.PhotoAdapter

class AuidosActivity : AppCompatActivity()  , GetAllFiles.OnFileReciveListener, FileRecievedListener {
    override fun Downloaded(list: List<FileDetails>) {
//        Glide.with(this).load(list.get(0)).into(btn)
        recyclerView.adapter = AudioAdapter(list)
    }

    override fun onFileRecive(id: String) {
        DriveServiceHelper.getInstance(mDriveService).GetFilesUrl(this,id, this).execute()
    }

    lateinit var photosBinding: ActivityPhotosBinding
    lateinit var btn: ImageView
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photosBinding = DataBindingUtil.setContentView(this, R.layout.activity_photos)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        setContentView(R.layout.activity_audio_recording)
        setTitle(resources.getString(R.string.current_years_photos))
        recyclerView = photosBinding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.itemAnimator = DefaultItemAnimator()

        GetAllFiles(this, "Chat Stocker audio", BaseActivity.mDriveServiceHelper, BaseActivity.mDriveService, this@AuidosActivity,"audio/mpeg").execute()


    }

    override fun onResume() {
        super.onResume()


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home)
            super.onBackPressed()
        return true
    }
}

