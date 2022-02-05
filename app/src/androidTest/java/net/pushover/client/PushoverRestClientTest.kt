package net.pushover.client

import com.makfc.notification_listener_service.BuildConfig
import junit.framework.TestCase

class PushoverRestClientTest : TestCase() {
    fun testPushMessage() {
        val messageResponse: MessageResponse = PushoverRestClient.pushMessage(
            PushoverMessage(
                apiToken = BuildConfig.APP_API_TOKEN,
                userId = BuildConfig.USER_ID_TOKEN,
                device = BuildConfig.DEVICE,
                priority = MessagePriority.HIGH, // HIGH|NORMAL|QUIET
                title = "title",
                message = "testing!",
                url = "https://github.com/sps/pushover4j",
                titleForURL = "pushover4j github repo",
            )
        )
        println("pushoverResponse: $messageResponse")
    }

    fun testGetSounds() {
        val sounds = PushoverRestClient.getSounds()
        println("sounds: $sounds")
    }
}