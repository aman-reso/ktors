package com.example.setup

import com.example.manager.Controller
import com.example.manager.GetPopularStations
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.netty.handler.codec.http.HttpResponse
import kotlinx.coroutines.coroutineScope

class TmApiNetwork {
    private val getPopularStations by lazy { GetPopularStations() }
    private val mainBaseController: MainBaseController by lazy { MainBaseController() }
    suspend fun startLogic() {
        val popularStations = getPopularStations.getPopularStations()
        popularStations.forEachIndexed { i, v ->
            popularStations.forEachIndexed { index, s ->
                if (i != index) {
                    coroutineScope {
                        mainBaseController.controlFunction(v, s) {
                            System.out.println("it-->$it")
                        }
                    }
                }
            }
        }
    }
}
