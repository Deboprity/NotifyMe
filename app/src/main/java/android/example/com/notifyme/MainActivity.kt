package android.example.com.notifyme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.example.com.notifyme.MainActivity.NotificationReceiver




class MainActivity : AppCompatActivity() {

    companion object {
        private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
        private const val NOTIFICATION_ID = 0
        private const val ACTION_UPDATE_NOTIFICATION = "android.example.com.notifyme.ACTION_UPDATE_NOTIFICATION"
    }

    private var buttonNotify: Button? = null
    private var buttonCancel: Button? = null
    private var buttonUpdate: Button? = null
    private var mNotifyManager: NotificationManager? = null
    private val mReceiver = NotificationReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(mReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION));

        buttonNotify = findViewById(R.id.notify)
        buttonNotify?.setOnClickListener { sendNotification() }

        buttonUpdate = findViewById(R.id.update)
        buttonUpdate?.setOnClickListener {
            //Update the notification
            updateNotification()
        }

        buttonCancel = findViewById(R.id.cancel)
        buttonCancel?.setOnClickListener {
            //Cancel the notification
            cancelNotification()
        }

        createNotificationChannel()
        setNotificationButtonState(isNotifyEnabled = true, isUpdateEnabled = false, isCancelEnabled = false)
    }

    override fun onDestroy() {
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }

    private fun sendNotification() {
        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        val updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT)
        val notifyBuilder = getNotificationBuilder()
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent)
        mNotifyManager?.notify(NOTIFICATION_ID, notifyBuilder.build())
        setNotificationButtonState(isNotifyEnabled = false, isUpdateEnabled = true, isCancelEnabled = true)
    }

    private fun createNotificationChannel() {
        mNotifyManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            val notificationChannel = NotificationChannel(PRIMARY_CHANNEL_ID, "Mascot Notification", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notification from Mascot"
            mNotifyManager?.createNotificationChannel(notificationChannel)
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setContentTitle("You've been notified!")
            .setContentText("This is your notification text.")
            .setSmallIcon(R.drawable.ic_android)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)
    }

    private fun updateNotification() {
        val androidImage = BitmapFactory.decodeResource(resources, R.drawable.mascot_1)
        val notifyBuilder = getNotificationBuilder()
        notifyBuilder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(androidImage).setBigContentTitle("Notification Updated!"))
        mNotifyManager?.notify(NOTIFICATION_ID, notifyBuilder.build())
        setNotificationButtonState(isNotifyEnabled = false, isUpdateEnabled = false, isCancelEnabled = true)
    }

    private fun cancelNotification() {
        mNotifyManager?.cancel(NOTIFICATION_ID)
        setNotificationButtonState(isNotifyEnabled = true, isUpdateEnabled = false, isCancelEnabled = false)
    }

    private fun setNotificationButtonState(isNotifyEnabled: Boolean, isUpdateEnabled: Boolean, isCancelEnabled: Boolean) {
        buttonNotify?.isEnabled = isNotifyEnabled
        buttonUpdate?.isEnabled = isUpdateEnabled
        buttonCancel?.isEnabled = isCancelEnabled
    }


    inner class NotificationReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            // Update the notification
        }
    }
}
