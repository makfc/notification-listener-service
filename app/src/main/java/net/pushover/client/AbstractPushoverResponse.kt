package net.pushover.client

abstract class AbstractPushoverResponse {
    abstract var status: Int
    abstract var request: String
}