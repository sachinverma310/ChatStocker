package stws.chatstocker.viewmodel

import android.content.DialogInterface
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import stws.chatstocker.ConstantsValues
import stws.chatstocker.utils.Prefrences

import java.io.File


class AccountDetailsViewModel:ViewModel() {
    fun deleteAccount(view:View){
        confirmationDialog(view)
    }

    private fun confirmationDialog(view: View) {
        AlertDialog.Builder(view.context)

                .setMessage("Are you sure you want to delete your account")

                .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, whichButton ->
                    dialog.dismiss()
//                    FirebaseAuth.getInstance().geA!!.delete().addOnSuccessListener(object :OnSuccessListener<Void>{
//                        override fun onSuccess(p0: Void?) {
                            Toast.makeText(view.context,"Account deleted successfully",Toast.LENGTH_SHORT).show()
                            Prefrences.saveBoolean(view.context,ConstantsValues.KEY_IS_LOGIN,false)
//
//                        }
//
//
//                    })
                    val user = FirebaseAuth.getInstance().currentUser
                    user!!.delete()
                    clearApplicationData(view)

                    (view.context as AppCompatActivity).finishAffinity()
                })
                .setNegativeButton(android.R.string.no, null).show()
    }
    fun clearApplicationData(view: View) {
        val cache = view.context.cacheDir
        val appDir = File(cache.getParent())
        if (appDir.exists()) {
            val children = appDir.list()
            for (s in children) {
                if (s != "lib") {
                    deleteDir(File(appDir, s))
                    Log.i("TAG", "File /data/data/APP_PACKAGE/$s DELETED")
                }
            }
        }
    }
    fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children!!.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }

        return dir!!.delete()
    }
}