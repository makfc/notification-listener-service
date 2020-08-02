package com.makfc.notification_listener_service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.session.MediaSession
import android.net.Uri
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.net.URLEncoder


class NotificationListener : NotificationListenerService() {
    companion object {
        const val TAG = BuildConfig.APPLICATION_ID
        const val CHANNEL_ID = TAG
    }

    override fun onListenerConnected() {
        Log.d(TAG, "onListenerConnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName == "org.blokada.alarm") return
//        if (sbn.packageName != "com.spotify.music") return
        val extras = sbn.notification.extras
        val template = extras.getCharSequence("android.template")
        if (template != "android.app.Notification\$MediaStyle") return
        Log.d(TAG, "extras template: ${template}")
        Log.d(TAG, "onNotificationPosted: $sbn")
        Log.d(TAG, "tickerText: ${sbn.notification.tickerText}")
        Log.d(TAG, "extras: ${extras}")
        Log.d(TAG, "extras android.subText (Title): ${extras.getCharSequence("android.subText")}")
        Log.d(TAG, "extras android.text (Artist): ${extras.getCharSequence("android.text")}")
/*        val token = extras.getParcelable<MediaSession.Token>("android.mediaSession")
        Log.d(TAG, "extras token: ${token}")
        if (token == null) return
        val mediaController = MediaController(this, token)
        Log.d(TAG, "playbackInfo: ${mediaController.playbackInfo}")
        Log.d(TAG, "playbackState: ${mediaController.playbackState}")
        Log.d(TAG, "metadata: ${mediaController.metadata}")
        Log.d(TAG, "queueTitle: ${mediaController.queueTitle}")
        Log.d(TAG, "mediaController.extras: ${mediaController.extras}")*/

        createNotificationChannel()
        val query = sbn.notification.tickerText.toString()
        val escapedQuery: String = URLEncoder.encode(query, "UTF-8")
        val escapedQuery2: String = URLEncoder.encode("歌詞翻譯 $query", "UTF-8")
        val uri: Uri = Uri.parse("http://www.google.com/#q=$escapedQuery")
        val uri2: Uri = Uri.parse("https://duckduckgo.com/?q=!ducky+$escapedQuery2")
        val uri3: Uri = Uri.parse("http://www.google.com/#q=$escapedQuery2")
        val uri4: Uri = Uri.parse("https://genius.com/search?q=$escapedQuery")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        val intent2 = Intent(Intent.ACTION_VIEW, uri2)
        val intent3 = Intent(Intent.ACTION_VIEW, uri3)
        val intent4 = Intent(Intent.ACTION_VIEW, uri4)
//        val intent = Intent(Intent.ACTION_WEB_SEARCH)
//        intent.putExtra(
//            SearchManager.QUERY,
//            sbn.notification.tickerText
//        ) // query contains search string
//        startActivity(intent)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val pendingIntent2: PendingIntent = PendingIntent.getActivity(this, 0, intent2, 0)
        val pendingIntent3: PendingIntent = PendingIntent.getActivity(this, 0, intent3, 0)
        val pendingIntent4: PendingIntent = PendingIntent.getActivity(this, 0, intent4, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(getString(R.string.google_search))
            .setContentText(sbn.notification.tickerText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .addAction(0, getString(R.string.lyric_translate_ducky), pendingIntent2)
            .addAction(0, getString(R.string.genius_search), pendingIntent4)
            .addAction(0, getString(R.string.lyric_translate_google), pendingIntent3)
            .setSound(null)
//            .setAutoCancel(true)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(0, builder.build())
            Log.d(TAG, "notify: $builder")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.d(TAG, "onNotificationRemoved: $sbn")
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setSound(null, null)
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}