package stws.chatstocker.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Chronometer
import androidx.databinding.DataBindingUtil
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityAudioRecordingBinding
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import android.os.SystemClock
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.media.MediaRecorder
import android.os.Environment

import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.api.services.drive.Drive
import stws.chatstocker.utils.DriveServiceHelper
import stws.chatstocker.utils.GetAllFiles
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
class AudioRecordingActivity : BaseActivity(),View.OnClickListener{
    private var fileName: String = ""
    lateinit var audioBinding:ActivityAudioRecordingBinding
    lateinit var chronometer:Chronometer;
    lateinit var imgAudiorecord:ImageView;
    lateinit var imgAudioStop:ImageView;
    lateinit var imgAudioPause:ImageView;
    lateinit var imgFile:ImageView;
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var recorder: MediaRecorder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioBinding=DataBindingUtil.setContentView(this,R.layout.activity_audio_recording)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        setContentView(R.layout.activity_audio_recording)
        setTitle(resources.getString(R.string.audio_rcording))
        chronometer=audioBinding.chronometer
        imgAudioPause=audioBinding.imgAudioPause
        imgAudioStop=audioBinding.imgStop
        imgFile=audioBinding.imgFile
        imgAudiorecord=audioBinding.imgAudio
        imgFile.setOnClickListener(this)
        imgAudioStop.setOnClickListener(this)
        chronometer.let {
            chronometer.setOnChronometerTickListener {
                chronometer = it;
            }
        }
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val file = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "/Chatstocker/Audio/")
        if (!file?.mkdirs()) {
            file.mkdir()
            Log.e(LOG_TAG, "Directory not created")
        }

        fileName= "$file" + File.separator+"Audio_" + timeStamp + "." + "3gp"
        Toast.makeText(this,"Recording started",Toast.LENGTH_SHORT).show()
        startStopAudioRecording(false)
    }
    fun startStopAudioRecording( isStart: Boolean){
        if (isStart) {
            chronometer.stop()
            stopRecording()
//            (view as Button).setText("Start")
        } else {
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()
            startRecording()

//            (view as Button).setText("Stop")
        }
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }
    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }

            start()
        }
    }
    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.imgStop->{
                startStopAudioRecording(true)
                imgAudioPause.visibility=View.INVISIBLE
                imgAudiorecord.visibility=View.VISIBLE
                Toast.makeText(this,"Recording saved",Toast.LENGTH_SHORT).show()
//                finish()
            }
            R.id.imgFile->{
//                startStopAudioRecording(false)
            }
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        GetAllFiles(this,"Chat Stocker audio",mDriveServiceHelper,mDriveService,File(fileName),"audio/mpeg").execute()
        recorder = null
    }

    override fun onStop() {
        super.onStop()
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }
}
