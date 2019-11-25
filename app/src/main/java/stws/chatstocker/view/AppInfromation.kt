package stws.chatstocker.view

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_app_infromation.*
import stws.chatstocker.ConstantsValues
import stws.chatstocker.R
import stws.chatstocker.databinding.ActivityAppInfromationBinding

class AppInfromation : AppCompatActivity() {
lateinit var activityFullscreenImageBinding:ActivityAppInfromationBinding
    lateinit var textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         activityFullscreenImageBinding = DataBindingUtil.setContentView(this, R.layout.activity_app_infromation)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        textView=activityFullscreenImageBinding.tvJustified;
        if (intent.getStringExtra(ConstantsValues.KEY_FROM).equals("appinfo")){
            setTitle("App Information")
            tvJustified.setText(resources.getString(R.string.app_information))
            tvJustified.setTypeface(null,Typeface.BOLD)
        }
       else if (intent.getStringExtra(ConstantsValues.KEY_FROM).equals("privacypolicy")){
            setTitle("Privacy Policy")
            tvJustified.setText(resources.getString(R.string.privacy))
            tvJustified.setTypeface(null,Typeface.NORMAL)
        }
        else if (intent.getStringExtra(ConstantsValues.KEY_FROM).equals("faqs")){
            setTitle("FAQ'S")
            tvJustified.setText(HtmlCompat.fromHtml("<div class=\"fh5co-section cmsPage\">\n" +
                    "  <div class=\"container\">\n" +
                    "   <div class=\"row\">\n" +
                    "       \n" +
                    "        <!-- Static content right side-->\n" +
                    "            <div class=\"col-sm-12 col-md-12\">\n" +
                    "                                <p><strong>Question: Is Stocker App safe?</strong></p>\n" +
                    "<p>Answer: Yes, it's completely safe.</p>\n" +
                    "<p><strong>Question: Can we chat with Stocker app?</strong></p>\n" +
                    "<p>Answer: Yes, you can chat there are two chat options here one is personal and second is a group.</p>\n" +
                    "<p><strong>Question: Can I use the app camera for selfies and videos?</strong></p>\n" +
                    "<p>Answer: Yes, you can use its camera for clicking pictures and videos. It automatically gets saved in the app.</p>\n" +
                    "<p><strong>Question: Does the app data occupy phone storage?</strong></p>\n" +
                    "<p>Answer: No, it doesn't occupy phone storage. It only occupies app space. Though it's a very light app and won't occupy much space.</p>\n" +
                    "<p><strong>Question:Can I recover my data in case of phone loss or damage?</strong></p>\n" +
                    "<p>Answer: Yes, you can recover your data by simply log in your Gmail account. With your mail ID, you can easily recover your app data.</p>\n" +
                    "<p><strong>Question:Do we need to pay anything for using Stocker app?</strong></p>\n" +
                    "<p>Answer:  No, it's absolutely free. You can download and use it without paying any single amount.</p>\n" +
                    "<p><strong>Question: Is it an Indian app?</strong></p>\n" +
                    "<p>Answer:  Yes, Stocker App is made in India.</p>\n" +
                    "          </div>\n" +
                    "            <!-- Static content end -->\n" +
                    "   </div>\n" +
                    "  </div>\n" +
                    "</div>    ",HtmlCompat.FROM_HTML_MODE_COMPACT))
        }
        else if (intent.getStringExtra(ConstantsValues.KEY_FROM).equals("tc")){
            setTitle("Terms & Conditions")
            tvJustified.setText(resources.getString(R.string.tc))
            tvJustified.setTypeface(null,Typeface.NORMAL)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home)
            super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
