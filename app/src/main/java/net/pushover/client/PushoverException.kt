package net.pushover.client

class PushoverException(message: String?, cause: Throwable?) : Exception(message, cause) {
    companion object {
        private const val serialVersionUID = 1L
    }
}