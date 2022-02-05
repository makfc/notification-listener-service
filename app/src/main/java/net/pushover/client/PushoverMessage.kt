package net.pushover.client

/**
 * Represents a Pushover message.
 * @property apiToken (required) - your application's API token
 * @property userId (required) - the user/group key (not e-mail address) of your user (or you), viewable when logged into our dashboard (often referred to as USER_KEY in our documentation and code examples)
 * @property message (required) - your message
 * @property device - your user's device name to send the message directly to that device, rather than all of the user's devices (multiple devices may be separated by a comma)
 * @property title - your message's title, otherwise your app's name is used
 * @property url - a supplementary URL to show with your message
 * @property titleForURL - a title for your supplementary URL, otherwise just the URL is shown
 * @property priority - send as -2 to generate no notification/alert, -1 to always send as a quiet notification, 1 to display as high-priority and bypass the user's quiet hours, or 2 to also require confirmation from the user
 * @property sound - the name of one of the sounds supported by device clients to override the user's default sound choice
 * @property timestamp - a Unix timestamp of your message's date and time to display to the user, rather than the time your message is received by our API
 */
data class PushoverMessage(
    var apiToken: String,
    val userId: String,
    val message: String,
    val htmlMessage: String? = null,
    val device: String? = null,
    val title: String? = null,
    val url: String? = null,
    val titleForURL: String? = null,
    val priority: MessagePriority = MessagePriority.NORMAL,
    val timestamp: Long? = null,
    val sound: String? = null
)
