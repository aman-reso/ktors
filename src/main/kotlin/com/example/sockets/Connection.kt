package com.example.sockets

import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        var lastId = AtomicInteger(0)
    }
    @get:Synchronized
    val name = "user${lastId.getAndIncrement()}"
}