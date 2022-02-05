package net.pushover.client

data class SoundsResponse(
    override var status: Int,
    override var request: String,
    var sounds: Map<String, String>?
) : AbstractPushoverResponse()