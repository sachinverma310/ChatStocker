package stws.chatstocker.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import stws.chatstocker.R
import stws.chatstocker.view.HomeActivity
import stws.chatstocker.view.fragments.UserFragment

class FirebaseService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.e("messs",remoteMessage.data.toString())
        if (remoteMessage.data != null) {
            showNotification(remoteMessage.data)
        }
    }

    private fun showNotification(title: Map<String,String>?) {
//

        val jsonObject=JSONObject(title!!.get("body"))
        val intent = Intent(this, UserFragment::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT)
        val channelId = getString(R.string.default_notification_channel_id)
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title!!.get("title"))
                .setContentText(jsonObject.getString("msg"))
                .setAutoCancel(true)
                .setSound(soundUri).setChannelId(channelId)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =  NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(1, notificationBuilder.build())
//        if (jsonObject.getString("dateTime")!!){
        FirebaseDatabase.getInstance().reference.child("chat_room")
                .child(jsonObject.getString("room_type"))
                .child(jsonObject.getString("dateTime"))
                .child("sentToserver").setValue(true)
//        onchatMessageSendResponse!!.postValue(chat)

    }
}