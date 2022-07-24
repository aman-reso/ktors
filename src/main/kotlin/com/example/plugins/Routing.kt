package com.example.plugins

import com.example.manager.Controller
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

fun Application.configureRouting() {

    // Starting point for a Ktor app:
    routing {
        get("/") {
            val params=this.call.parameters
            var commandKey:Int=2
            var date:String?=null
            var oCode:String?=null
            var dcode:String?=null
            val dateLimit: Int =1
            if (params.contains("command")){
                commandKey= params["command"]?.toInt()?:2
            }
            if (params.contains("ocode")){
                oCode= params["ocode"]
            }
            if (params.contains("dcode")){
                dcode= params["dcode"]
            }
            call.respond("Hello")
//            if (params.contains("dateLimit")){
//                dateLimit=params["dateLimit"]?.toInt()?:1
//            }
//
//            Controller.start(commandKey,oCode,dcode, dateLimit) {
//                println("response-->$it")
//                call.respond(it)
//            }
//            call.respondText("Hello World!")
        }
    }
    routing {
    }
}
