package stws.chatstocker.view

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.databinding.DataBindingUtil
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityVideoPlayerBinding
import android.media.AudioManager
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.SurfaceView
import android.widget.MediaController

import android.media.AudioAttributes
import android.os.Build
import android.os.Environment
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient


import android.webkit.WebView
import androidx.lifecycle.ViewModelProviders
import stws.chatstocker.model.FileDetails
import stws.chatstocker.viewmodel.PhotoViewModel
import java.io.File


class VideoPlayerActivity : AppCompatActivity(), SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {
    lateinit var mediaPlayer: MediaPlayer
    private lateinit var vidHolder: SurfaceHolder
    private lateinit var vidSurface: SurfaceView
    private lateinit var vidAddress: String
    private lateinit var vidControl: MediaController
   private val photo= File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Chatstocker")
    override fun onPrepared(mp: MediaPlayer?) {
        mp!!.start();
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        mediaPlayer = MediaPlayer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer.setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build())
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
        try {
            mediaPlayer.setOnPreparedListener({ mp -> mp.start() })
            mediaPlayer.setDataSource(vidAddress)
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        try {
//            mediaPlayer = MediaPlayer()
//            mediaPlayer.setDisplay(vidHolder)
//            mediaPlayer.setDataSource(vidAddress)
//            mediaPlayer.prepare()
//            mediaPlayer.setOnPreparedListener(this)
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityVideoPlayerBinding = DataBindingUtil.setContentView<ActivityVideoPlayerBinding>(this, R.layout.activity_video_player)
        val videoId = intent.getStringExtra(ConstantsValues.KEY_VIDEO_ID)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        vidAddress = ConstantsValues.BASE_FILE_URL + videoId + "/preview"
//        vidSurface = activityVideoPlayerBinding.surfView
//
//
//        vidControl =  MediaController(this);
//
//        vidHolder = vidSurface.getHolder();
//        vidHolder.addCallback(this);
//        vidControl.setAnchorView(vidSurface);
        val viewModel= ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        val photos=intent.getParcelableExtra<FileDetails>(ConstantsValues.KEY_FILE)
        viewModel.photos=photos
        viewModel.isVideo=false
        viewModel.context=this
        viewModel.drive=BaseActivity.mDriveService
        activityVideoPlayerBinding.viewModel=viewModel
        val photoFile=File(photo.path+File.separator
                + videoId+"." + "mp4")
      val videoView =activityVideoPlayerBinding.surfView
        //Set MediaController  to enable play, pause, forward, etc options.
        val mediaController=  MediaController(this);
        mediaController.setAnchorView(videoView);
        //Location of Media File
        var uri = Uri.parse(photoFile.absolutePath)
        //Starting VideView By Setting MediaController and URI
        videoView.setMediaController(mediaController);
        if (intent.getBooleanExtra(ConstantsValues.KEY_ISFROM_CHAT,false))
            uri = Uri.parse(intent.getStringExtra(ConstantsValues.KEY_FILE_URL));
//            videoView.setVideoPath(intent.getStringExtra(ConstantsValues.KEY_FILE_URL));
//        else
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
//        vidAddress="<html><head></head><body><iframe width='100%' height=100%' src='https://drive.google.com/file/d/"+videoId+"/preview'></iframe></body></html>"
//        webview.loadData(vidAddress,"text/html; charset=utf-8", null)

//        vidSurface.setMediaController(vidControl);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home)
            super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
