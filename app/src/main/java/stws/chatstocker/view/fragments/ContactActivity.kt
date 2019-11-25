package stws.chatstocker.view.fragments

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil

import stws.chatstocker.databinding.ActivityContactBinding
import android.widget.Toast
import android.content.Intent
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.MenuItem
import stws.chatstocker.R


class ContactActivity : AppCompatActivity() {
    lateinit var activityContaBinding:ActivityContactBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityContaBinding =DataBindingUtil.setContentView(this, R.layout.activity_contact)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val btnSend=activityContaBinding.btnSend
        val edtMessage=activityContaBinding.edtMessage
        val edtSubject=activityContaBinding.edtSubject
        btnSend.setOnClickListener(View.OnClickListener {
          val  i = Intent(Intent.ACTION_SEND)
            i.setType("message/rfc822")
            i.putExtra(Intent.EXTRA_EMAIL, arrayOf("recipient@example.com"))
            i.putExtra(Intent.EXTRA_SUBJECT, edtSubject.text.toString())
            i.putExtra(Intent.EXTRA_TEXT, edtMessage.text.toString())
            try {
                startActivity(Intent.createChooser(i, "Send mail..."))
            } catch (ex: android.content.ActivityNotFoundException) {
                Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
            }

        })
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home)
            super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
