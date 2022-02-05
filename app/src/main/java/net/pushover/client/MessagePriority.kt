@file:Suppress("unused", "unused", "unused", "unused")

package net.pushover.client

enum class MessagePriority(private val priority: Int) {
    LOWEST(-2), LOW(-1), QUIET(-1), NORMAL(0), HIGH(1), EMERGENCY(2);

    override fun toString(): String {
        return priority.toString()
    }
}