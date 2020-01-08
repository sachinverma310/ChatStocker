package stws.chatstocker.view

import android.Manifest
import android.content.DialogInterface
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
import android.view.LayoutInflater
import android.view.MenuItem

import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.api.services.drive.Drive
import kotlinx.android.synthetic.main.activity_chat.*
import stws.chatstocker.utils.DriveServiceHelper
import stws.chatstocker.utils.GetAllFiles
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val LOG_TAG = "AudioRecordTest"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class AudioRecordingActivity : BaseActivity(), View.OnClickListener {
    private var fileName: String = ""
    lateinit var audioBinding: ActivityAudioRecordingBinding
    lateinit var chronometer: Chronometer;
    lateinit var imgAudiorecord: ImageView;
    lateinit var imgAudioStop: ImageView;
    lateinit var imgAudioPause: ImageView;
    lateinit var imgPlay: ImageView;
    lateinit var imgPause: ImageView;
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var recorder: MediaRecorder? = null
    private var isRecordingStarted: Boolean? = false
    private lateinit var timeStamp: String
    private lateinit var file: File
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioBinding = DataBindingUtil.setContentView(this, R.layout.activity_audio_recording)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        setContentView(R.layout.activity_audio_recording)
        setTitle(resources.getString(R.string.audio_rcording))
        chronometer = audioBinding.chronometer
        imgAudioPause = audioBinding.imgAudioPause
        imgAudioStop = audioBinding.imgStop
        imgPlay = audioBinding.imgPlay
        imgPause = audioBinding.imgPause
        imgAudiorecord = audioBinding.imgAudio
        imgPause.setOnClickListener(this)
        imgPlay.setOnClickListener(this)
        imgAudioStop.setOnClickListener(this)
        chronometer.let {
            chronometer.setOnChronometerTickListener {
                chronometer = it;
            }
        }
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        file = File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "/Chatstocker/Audio/")
        if (!file?.mkdirs()) {
            file.mkdir()
            Log.e(LOG_TAG, "Directory not created")
        }

        fileName = "$file" + File.separator + "Audio_" + timeStamp + "." + "mp3"

    }

    fun startStopAudioRecording(isStart: Boolean) {
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
        when (v!!.id) {
            R.id.imgStop -> {
                startStopAudioRecording(true)
                imgAudioPause.visibility = View.INVISIBLE
                imgAudiorecord.visibility = View.VISIBLE

                imgPlay.visibility = View.VISIBLE
                imgPause.visibility = View.GONE
                isRecordingStarted = false
//                finish()
            }
            R.id.imgPlay -> {
                Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
                if (!isRecordingStarted!!)
                    startStopAudioRecording(false)
                else {
                    recorder!!.resume()
                    chronometer.start()
                }
                imgPlay.visibility = View.GONE
                imgPause.visibility = View.VISIBLE
                isRecordingStarted = true

//                chronometer.stop()
//                startStopAudioRecording(false)
            }
            R.id.imgPause -> {
                imgPlay.visibility = View.VISIBLE
                imgPause.visibility = View.GONE
                recorder!!.pause()
                chronometer.stop()
//                startStopAudioRecording(false)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home)
        super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
            openeRenameDialog()
        }

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

    fun openeRenameDialog() {
        val li = LayoutInflater.from(this);
        val promptsView = li.inflate(R.layout.file_rename_dialog, null);
        val alertDailogBuilder = AlertDialog.Builder(this)
        alertDailogBuilder.setView(promptsView)

        val userInput = promptsView.findViewById(R.id.editTextDialogUserInput) as EditText
        alertDailogBuilder

                .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, whichButton ->
                    dialog.dismiss()
                    val from = File(file, "Audio_" + timeStamp + ".mp3");
                    var to: File? = null
                    if (userInput.length() > 0)
                        to = File(file, userInput.text.toString() + ".mp3")
                    else
                        to = File(fileName)
                    if (from.exists())
                        from.renameTo(to);
                    GetAllFiles(this, "Chat Stocker audio", mDriveServiceHelper, mDriveService, to!!, "audio/mpeg").execute()
                    Toast.makeText(this, "Recording saved", Toast.LENGTH_SHORT).show()
                })
                .setNegativeButton(android.R.string.no, null).show()
    }
}
