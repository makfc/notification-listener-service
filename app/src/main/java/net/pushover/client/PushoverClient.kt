package net.pushover.client

interface PushoverClient {
    @Throws(PushoverException::class)
    fun pushMessage(msg: PushoverMessage): MessageResponse

    @Throws(PushoverException::class)
    fun getSounds(): Set<PushOverSound>
}