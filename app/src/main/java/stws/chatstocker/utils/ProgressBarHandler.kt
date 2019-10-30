package stws.chatstocker.utils

import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import stws.chatstocker.R
import java.lang.Exception

class ProgressBarHandler() {
    companion object{
        private  var progressBarHandler:ProgressBarHandler?=null
        private lateinit var alertDialog: AlertDialog
        private lateinit var fragmentManager: FragmentManager

         fun getInstance():ProgressBarHandler{
            if (progressBarHandler==null){
                progressBarHandler= ProgressBarHandler()
            }
            return progressBarHandler as ProgressBarHandler
        }
        fun show(context:Context){
            fragmentManager = (context as AppCompatActivity).supportFragmentManager;
            alertDialog = AlertDialog.Builder(context).create()
            val layout = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.progress_bar_layout, null)

            val progressBar = layout.findViewById(R.id.progressBar) as ProgressBar
            this.alertDialog.requestWindowFeature(1);
            val lWindowParams =  WindowManager.LayoutParams();
            lWindowParams.copyFrom(this.alertDialog.getWindow()?.getAttributes());
            lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT; // this is where the magic happens
            lWindowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            this.alertDialog.setView(layout);
            this.alertDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(0));
            this.alertDialog.getWindow()?.setAttributes(lWindowParams);
            this.alertDialog.setCancelable(false);
            try {

            alertDialog.show()
            }
            catch (e:Exception){

            }
        }
        fun hide(){
            alertDialog.hide()
        }
    }
}