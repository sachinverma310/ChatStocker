package stws.chatstocker.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityPhotosBinding

class PhotosActivity : AppCompatActivity() {
    lateinit var photosBinding: ActivityPhotosBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photosBinding = DataBindingUtil.setContentView(this, R.layout.activity_photos)
        val recyclerView = photosBinding.recyclerView
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.itemAnimator=DefaultItemAnimator()
    }
}
