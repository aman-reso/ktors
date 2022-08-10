package com.example.plugins

import com.example.manager.Controller
import com.example.setup.MainBaseController
import com.example.setup.TmApiNetwork
import com.example.sockets.Connection
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import io.ktor.websocket.serialization.*
import kotlinx.coroutines.runBlocking
import java.net.http.WebSocket
import java.util.*

fun Application.configureRouting() {

    // Starting point for a Ktor app:
    routing {
        get("/") {
            try {
                val params = this.call.parameters
                var commandKey: Int = 2
                var date: String? = null
                var oCode: String? = null
                var dcode: String? = null
                val dateLimit: Int = 1
                if (params.contains("command")) {
                    commandKey = params["command"]?.toInt() ?: 2
                }
                if (params.contains("ocode")) {
                    oCode = params["ocode"]
                }
                if (params.contains("dcode")) {
                    dcode = params["dcode"]
                }

                Controller.start(commandKey, oCode, dcode, dateLimit) {
                    println("response-->$it")
                    call.respond(it)
                }
            } catch (e: Exception) {
                call.respond("Error-->${e.localizedMessage}")
            }
        }
    }
    routing {
        get("/single") {
            System.out.println(System.currentTimeMillis())
            try {
                runBlocking {
                    TmApiNetwork().startLogic {
                        println(it)
                        System.out.println(System.currentTimeMillis())
                        call.respond(it)
                    }
                }
            } catch (e: Exception) {
                call.respond("error-->${e.localizedMessage}")
            }
//            call.respond("hii")
        }
    }

    routing {
        get("/pnr") {
            val parameters = this.call.parameters
            if (parameters.contains(PNR_NUM_KEY)) {
                val pnrNumber = parameters[PNR_NUM_KEY]
                //when receive parameter
                call.respond("we have received pnr:-$pnrNumber")
                connections.forEach {
                    if (pnrNumber != null) {
                        if (pnrNumber.length==10){
                            val requestByUser = RequestByUser("pnr", pnrNumber)
                            val x = it.session as WebSocketServerSession
                            x.sendSerialized(requestByUser)
                        }else{
                            call.respond("please send the correct pnr number")
                        }
                    }
                }
            } else {
                call.respond("we didn't received pnr")
            }
        }
    }
}

const val PNR_NUM_KEY = "pnr_num"
val connections: MutableSet<Connection> = Collections.synchronizedSet(LinkedHashSet())

data class RequestByUser(var type: String, var pnrNumber: String)
