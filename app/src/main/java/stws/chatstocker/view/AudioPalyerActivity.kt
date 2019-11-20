package stws.chatstocker.view

import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityAudioPalyerBinding
import androidx.databinding.adapters.SeekBarBindingAdapter.setProgress
import androidx.core.view.ViewCompat.setX
import android.opengl.ETC1.getWidth
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.MenuItem

import android.view.View
import androidx.lifecycle.ViewModelProviders
import stws.chatstocker.ConstantsValues
import stws.chatstocker.ConstantsValues.KEY_ISFROM_CHAT
import stws.chatstocker.ConstantsValues.KEY_PATH
import stws.chatstocker.model.FileDetails
import stws.chatstocker.viewmodel.AudioViewModel
import stws.chatstocker.viewmodel.PhotoViewModel


class AudioPalyerActivity : AppCompatActivity(),Runnable,ConstantsValues  {
    var mediaPlayer:MediaPlayer? = null;
    lateinit var seekBar: SeekBar
     var wasPlaying:Boolean = false
    lateinit var fab:FloatingActionButton
    lateinit var path:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=DataBindingUtil.setContentView<ActivityAudioPalyerBinding>(this,R.layout.activity_audio_palyer)
        fab=binding.button
        val viewModel= ViewModelProviders.of(this).get(AudioViewModel::class.java)
        val photos=intent.getParcelableExtra<FileDetails>(ConstantsValues.KEY_FILE)
        if (intent.getBooleanExtra(KEY_ISFROM_CHAT,false))
            binding.bottomLayout.visibility=View.GONE
        viewModel.context=this
        viewModel.drive=BaseActivity.mDriveService
        viewModel.photos=photos
        viewModel.fileName= intent.getStringExtra(KEY_PATH)
        binding.viewModel=viewModel
        seekBar=binding.seekbar
        val seekBarHint=binding.textView
        path=intent.getStringExtra(KEY_PATH)
        mediaPlayer= MediaPlayer()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setTitle(resources.getString(R.string.audio))
        fab.setOnClickListener(View.OnClickListener {
            playSong()
        })
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar) {

                seekBarHint.setVisibility(View.VISIBLE)
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromTouch: Boolean) {
                seekBarHint.setVisibility(View.VISIBLE)
                val x = Math.ceil((progress / 1000f).toDouble()).toInt()

                if (x < 10)
                    seekBarHint.setText("0:0$x")
                else
                    seekBarHint.setText("0:$x")

                val percent = progress / seekBar.max.toDouble()
                val offset = seekBar.thumbOffset
                val seekWidth = seekBar.width
                val `val` = Math.round(percent * (seekWidth - 2 * offset)).toInt()
                val labelWidth = seekBarHint.getWidth()
                seekBarHint.setX(offset.toFloat() + seekBar.x + `val`.toFloat()
                        - Math.round(percent * offset).toFloat()
                        - Math.round(percent * labelWidth / 2).toFloat())

                if (progress > 0 && mediaPlayer != null && !mediaPlayer!!.isPlaying) {
                    clearMediaPlayer()
                    fab.setImageDrawable(ContextCompat.getDrawable(this@AudioPalyerActivity, android.R.drawable.ic_media_play))
                    this@AudioPalyerActivity.seekBar.setProgress(0)
                }

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {


                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.seekTo(seekBar.progress)
                }
            }
        })
    }

    public fun playSong() {

        try {


            if (mediaPlayer != null && mediaPlayer!!.isPlaying()) {
                clearMediaPlayer();
                seekBar.setProgress(0);
                wasPlaying = true;
                fab.setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.ic_media_play));
            }


            if (!wasPlaying) {

                if (mediaPlayer == null) {
                    mediaPlayer =  MediaPlayer();
                }

                fab.setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.ic_media_pause));

//                val descriptor: AssetFileDescriptor = getAssets().openFd("suits.mp3");
//                mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
//                descriptor.close();

                mediaPlayer!!.setDataSource(path)
                mediaPlayer!!.prepare();
                mediaPlayer!!.setVolume(0.5f, 0.5f);
                mediaPlayer!!.setLooping(false);
                seekBar.setMax(mediaPlayer!!.getDuration());

                mediaPlayer!!.start();
                 Thread(this).start();

            }

            wasPlaying = false;
        } catch ( e:Exception) {
            e.printStackTrace();

        }
    }

    public override fun run() {

        var currentPosition = mediaPlayer!!.getCurrentPosition();
        val total = mediaPlayer!!.getDuration();


        while (mediaPlayer != null && mediaPlayer!!.isPlaying() && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mediaPlayer!!.getCurrentPosition();
            } catch ( e:InterruptedException) {
                return;
            } catch ( e:Exception) {
                return;
            }

            seekBar.setProgress(currentPosition);

        }
    }


    protected override fun onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null && mediaPlayer!!.isPlaying())
        clearMediaPlayer();
    }

      fun  clearMediaPlayer() {

        mediaPlayer!!.stop();
        mediaPlayer!!.release();
        mediaPlayer = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home)
            super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
