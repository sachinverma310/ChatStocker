package stws.chatstocker.utils

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import stws.chatstocker.R

class RecordingPopup {
    companion object{
        private  var recordingPopup:RecordingPopup?=null
        private lateinit var alertDialog: AlertDialog
        private lateinit var fragmentManager: FragmentManager

        fun getInstance():RecordingPopup{
            if (recordingPopup==null){
                recordingPopup= RecordingPopup()
            }
            return recordingPopup as RecordingPopup
        }
        fun show(context: Context){
            fragmentManager = (context as AppCompatActivity).supportFragmentManager;
            alertDialog = AlertDialog.Builder(context).create()
            val layout = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.audio_recording_popup, null)

//            val progressBar = layout.findViewById(R.id.progressBar) as ProgressBar
            this.alertDialog.requestWindowFeature(1);
            val lWindowParams =  WindowManager.LayoutParams();
            lWindowParams.copyFrom(this.alertDialog.getWindow()?.getAttributes());
            lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT; // this is where the magic happens
            lWindowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            this.alertDialog.setView(layout);
            this.alertDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(0));
            this.alertDialog.getWindow()?.setAttributes(lWindowParams);
            this.alertDialog.setCancelable(false);
            alertDialog.show()
        }
        fun hide(){
            alertDialog.hide()
        }
    }
}