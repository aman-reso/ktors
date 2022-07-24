package com.example.plugins

import com.example.manager.Controller
import com.example.setup.MainBaseController
import com.example.setup.TmApiNetwork
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking

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
            runBlocking {
                TmApiNetwork().startLogic()
            }
            call.respond("hii")
        }
    }
}

