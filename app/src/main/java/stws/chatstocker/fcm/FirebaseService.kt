package stws.chatstocker.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import stws.chatstocker.R
import stws.chatstocker.view.HomeActivity
import stws.chatstocker.view.fragments.UserFragment
import java.util.regex.Pattern

import com.bumptech.glide.request.target.AppWidgetTarget
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.media.AudioAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.NotificationTarget
import com.google.firebase.database.ServerValue
import stws.chatstocker.ConstantsValues
import stws.chatstocker.utils.Prefrences


class FirebaseService : FirebaseMessagingService() {
    val URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.e("messs", remoteMessage.data.toString())
        if (remoteMessage.data != null) {
            showNotification(remoteMessage.data)
        }
    }

    private fun showNotification(title: Map<String, String>?) {
//

        val jsonObject = JSONObject(title!!.get("body"))
        var isGroup:Boolean=false
        if (jsonObject.has("isgroup"))
            isGroup=true;
        val intent = Intent(this, UserFragment::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT)
        var channelId = getString(R.string.default_notification_channel_id)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationLayout = RemoteViews(packageName, R.layout.notification_small)
        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.notification_small)
//        val tvTitle=
        notificationLayout.setTextViewText(R.id.notification_title, title!!.get("title"))
        val mAppWidgetTarget = AppWidgetTarget(this, R.id.imgMessage, notificationLayout, R.id.imgMessage) as AppWidgetTarget


// Apply the layouts to the notification
        var customNotification: Notification? = null
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notiSetting = Prefrences.getStringValue(this, ConstantsValues.KEY_NotI_SETTING)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val attributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()
            if (notiSetting != null) {
                if (notiSetting=="Vibrate") {
                    notificationManager.deleteNotificationChannel(channelId);
                    channelId="Channel vibrate"
                    val channel = NotificationChannel(channelId,
                            "Channel With no sound",
                            NotificationManager.IMPORTANCE_LOW);
                    channel.setShowBadge(true);
                    channel.setSound(null, null)
                    channel.enableVibration(true);
                    notificationManager.createNotificationChannel(channel);
                }
                else if (notiSetting=="Silent") {
                    notificationManager.deleteNotificationChannel(channelId);
                    channelId="Channel silent"
                    val channel = NotificationChannel(channelId,
                            "Channel With no sound",
                            NotificationManager.IMPORTANCE_LOW);
                    channel.setShowBadge(true);
                    channel.setSound(null, null)
                    notificationManager.createNotificationChannel(channel);
                } else {
                    notificationManager.deleteNotificationChannel(channelId);
                    channelId="Channel With sound"
                    val channel = NotificationChannel(channelId,
                            "Channel With  sound",
                            NotificationManager.IMPORTANCE_HIGH);
                    channel.setShowBadge(true);
                    channel.setSound(soundUri, attributes)
                    notificationManager.createNotificationChannel(channel);
                }

            }


        }

        if (notiSetting != null) {
            if (notiSetting.equals("Vibrate")) {
                val vibrater = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    vibrater.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                else
                    vibrater.vibrate(1000)
                customNotification = NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                        .setContentTitle(title!!.get("title"))
                        .setContent(notificationLayout)
                        .setVibrate(listOf(1L, 2L, 3L).toLongArray())
                        .setContentIntent(pendingIntent)
                        .build()
            } else if (notiSetting.equals("Silent")) {
                customNotification = NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                        .setContentTitle(title!!.get("title"))
                        .setContent(notificationLayout)
                        .setContentIntent(pendingIntent)
                        .build()
            } else if (notiSetting.equals("Tone")) {
                customNotification = NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                        .setContentTitle(title!!.get("title"))
                        .setContent(notificationLayout)
                        .setSound(soundUri)
                        .setContentIntent(pendingIntent)
                        .build()
            }
        } else
            customNotification = NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setContentTitle(title!!.get("title"))
                    .setContent(notificationLayout)
                    .setSound(soundUri)
                    .setContentIntent(pendingIntent)
                    .build()
        val notificationTarget = NotificationTarget(
                this,
                R.id.imgMessage,
                notificationLayout,
                customNotification,
                1);
        val p = Pattern.compile(URL_REGEX)
        val matcher = p.matcher(jsonObject.getString("msg"));//replace with string to compare
        if (matcher.find()) {
            notificationLayout.setViewVisibility(R.id.imgMessage, View.VISIBLE);
            Glide.with(this).asBitmap()
                    .load(jsonObject.getString("msg"))
                    .into(notificationTarget);
            notificationLayout.setViewVisibility(R.id.tvMessage, View.GONE);

        } else {
            notificationLayout.setViewVisibility(R.id.tvMessage, View.VISIBLE);
            notificationLayout.setViewVisibility(R.id.imgMessage, View.GONE);
            notificationLayout.setTextViewText(R.id.tvMessage, jsonObject.getString("msg"))
        }
//        val notificationBuilder = NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(title!!.get("title"))
//                .setContentText(jsonObject.getString("msg"))
//                .setAutoCancel(true)
//                .setSound(soundUri).setChannelId(channelId)
//                .setContentIntent(pendingIntent)





        notificationManager.notify(1, customNotification)
//        if (jsonObject.getString("dateTime")!!){
        val userId=Prefrences.getUserDetails(this,"login_data").uid
        FirebaseDatabase.getInstance().reference.child("User")
                .child(userId!!)
                .child("friend")
                .child(jsonObject.getString("senderUid")).child("last_msg_time").setValue(ServerValue.TIMESTAMP)
        if (!isGroup) {
            FirebaseDatabase.getInstance().reference.child("chat_room")
                    .child(jsonObject.getString("room_type"))
                    .child(jsonObject.getString("dateTime"))
                    .child("sentToserver").setValue(true)
        }
//        onchatMessageSendResponse!!.postValue(chat)

    }
}