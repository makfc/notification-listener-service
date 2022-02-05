package net.pushover.client

data class MessageResponse(
    override var status: Int,
    override var request: String
) : AbstractPushoverResponse()