package com.makfc.notification_listener_service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.pushover.client.MessageResponse
import net.pushover.client.PushoverMessage
import net.pushover.client.PushoverRestClient
import java.net.URLEncoder


class NotificationListener : NotificationListenerService() {
    companion object {
        const val TAG = BuildConfig.APPLICATION_ID
    }

    override fun onListenerConnected() {
        Log.d(TAG, "onListenerConnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName == "org.blokada.alarm") return
//        if (sbn.packageName != "com.spotify.music") return

        val extras = sbn.notification.extras
        val template = extras.getCharSequence("android.template")
        val channelId = sbn.notification.channelId
        Log.d(TAG, "channelId: $channelId")
        if (!template.isNullOrBlank() && listOf(
                "com.whatsapp",
                "org.thoughtcrime.securesms"
            ).contains(sbn.packageName)
        ) {
            val pm = applicationContext.packageManager
            val applicationInfo: ApplicationInfo? = try {
                pm.getApplicationInfo(sbn.packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
            // channelId individual_chat_defaults_4
            // template: android.app.Notification$InboxStyle -> channel message count
            // template: android.app.Notification$MessagingStyle -> message
            val applicationName =
                (if (applicationInfo != null) pm.getApplicationLabel(applicationInfo) else "(unknown)") as String
            val message = "${extras.getCharSequence(Notification.EXTRA_TITLE)}: ${
                extras.getCharSequence(
                    Notification.EXTRA_TEXT
                )
            }\nchannelId:${channelId}\ntemplate:${template}"
            GlobalScope.launch(Dispatchers.IO) {
                val messageResponse: MessageResponse = PushoverRestClient.pushMessage(
                    PushoverMessage(
                        apiToken = BuildConfig.APP_API_TOKEN,
                        userId = BuildConfig.USER_ID_TOKEN,
                        device = BuildConfig.DEVICE,
                        title = applicationName,
                        message = message,
                    )
                )
                Log.d(TAG, "messageResponse: $messageResponse")
            }
            return
        }
        if (template == "android.app.Notification\$MediaStyle") {
            Log.d(TAG, "extras template: $template")
            Log.d(TAG, "onNotificationPosted: $sbn")
            Log.d(TAG, "tickerText: ${sbn.notification.tickerText}")
            if (extras != null) {
                Log.d(TAG, "extras: $extras")
                Log.d(
                    TAG,
                    "extras android.subText (Title): " +
                            extras.getCharSequence(Notification.EXTRA_SUB_TEXT)
                )
                Log.d(
                    TAG, "extras android.text (Artist): " +
                            extras.getCharSequence(Notification.EXTRA_TEXT)
                )
            }
/*
            val token = extras.getParcelable<MediaSession.Token>("android.mediaSession")
            Log.d(TAG, "extras token: ${token}")
            if (token == null) return
            val mediaController = MediaController(this, token)
            Log.d(TAG, "playbackInfo: ${mediaController.playbackInfo}")
            Log.d(TAG, "playbackState: ${mediaController.playbackState}")
            Log.d(TAG, "metadata: ${mediaController.metadata}")
            Log.d(TAG, "queueTitle: ${mediaController.queueTitle}")
            Log.d(TAG, "mediaController.extras: ${mediaController.extras}")
*/

            createNotificationChannel()
            val query =
                sbn.notification.tickerText?.toString() ?: extras.getCharSequence("android.title")
                    ?.toString()
            val escapedQuery: String = URLEncoder.encode(query, "UTF-8")
            val escapedQuery2: String = URLEncoder.encode("歌詞翻譯 $query", "UTF-8")
            val uri: Uri = Uri.parse("http://www.google.com/search?q=$escapedQuery")
            val uri2: Uri = Uri.parse("https://duckduckgo.com/?q=!ducky+$escapedQuery2")
            val uri3: Uri = Uri.parse("http://www.google.com/search?q=$escapedQuery2")
            val uri4: Uri = Uri.parse("https://genius.com/search?q=$escapedQuery")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            val intent2 = Intent(Intent.ACTION_VIEW, uri2)
            val intent3 = Intent(Intent.ACTION_VIEW, uri3)
            val intent4 = Intent(Intent.ACTION_VIEW, uri4)
//            val intent = Intent(Intent.ACTION_WEB_SEARCH)
//            intent.putExtra(
//                SearchManager.QUERY,
//                sbn.notification.tickerText
//            ) // query contains search string
//            startActivity(intent)
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                this, 0,
                intent, PendingIntent.FLAG_IMMUTABLE
            )
            val pendingIntent2: PendingIntent = PendingIntent.getActivity(
                this, 0,
                intent2, PendingIntent.FLAG_IMMUTABLE
            )
            val pendingIntent3: PendingIntent = PendingIntent.getActivity(
                this, 0,
                intent3, PendingIntent.FLAG_IMMUTABLE
            )
            val pendingIntent4: PendingIntent = PendingIntent.getActivity(
                this, 0,
                intent4, PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(this, R.string.channel_name.toString())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(getString(R.string.google_search))
                .setContentText(query)
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
        } else if (channelId == "phone_incoming_call") {
            createNotificationChannel2()
            var query =
                sbn.notification.tickerText?.toString() ?: extras.getCharSequence("android.title")
                    ?.toString()
            query = query?.replace(" ", "")
            val escapedQuery: String = URLEncoder.encode(query, "UTF-8")
            val uri: Uri = Uri.parse("http://www.google.com/search?q=$escapedQuery")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                this, 0,
                intent, PendingIntent.FLAG_IMMUTABLE
            )
            val builder = NotificationCompat.Builder(this, R.string.channel_name2.toString())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(getString(R.string.google_search))
                .setContentText(query)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setSound(null)
//            .setAutoCancel(true)
            with(NotificationManagerCompat.from(this)) {
                // notificationId is a unique int for each notification that you must define
                notify(1, builder.build())
                Log.d(TAG, "notify: $builder")
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.d(TAG, "onNotificationRemoved: $sbn")
        Log.d(TAG, "extras: ${sbn.notification.extras}")
        if (sbn.packageName != "com.spotify.music") return
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            cancel(0)
            Log.d(TAG, "cancel: id:0")
        }
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel =
            NotificationChannel(R.string.channel_name.toString(), name, importance).apply {
                description = descriptionText
                setSound(null, null)
            }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotificationChannel2() {
        val name = getString(R.string.channel_name2)
        val descriptionText = getString(R.string.channel_description2)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel =
            NotificationChannel(R.string.channel_name2.toString(), name, importance).apply {
                description = descriptionText
                setSound(null, null)
            }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}