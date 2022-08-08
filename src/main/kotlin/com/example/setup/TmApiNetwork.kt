package com.example.setup

import com.example.manager.GetPopularStations
import com.example.manager.Response
import com.example.manager.SingleItem
import com.example.utility.CommonUtils
import kotlinx.coroutines.*

class TmApiNetwork {
    private val getPopularStations by lazy { GetPopularStations() }
    private val mainBaseController: MainBaseController by lazy { MainBaseController() }
    private var requests = java.util.Collections.synchronizedList(mutableListOf<Deferred<SingleItem>>())
    suspend fun startLogic(callback: suspend (Response) -> Unit) {
        runBlocking {
            val popularStations =  getPopularStations.getPopularStations()
                //GetTrainStationCode.getAllStationCode().subList(0, 20)

            val irctcDate: String = CommonUtils.dateFormatForIrctc(0)
            val tmDate: String = CommonUtils.dateFormatForTrainMan(0)
            val doesRun: String = CommonUtils.getDay()
            val doesRunIndex: Int = CommonUtils.doesRunWithTM()
            popularStations.forEachIndexed { i, v ->
                popularStations.forEachIndexed { index, s ->
                    if (i != index) {
                        val request = async { mainBaseController.controlFunction(v, s, irctcDate, tmDate, doesRun, doesRunIndex) }
                        System.out.println(request.await())
                        requests.add(request)
                    }
                }
            }
            val response = Response()
            response.date = tmDate
            val list = ArrayList<SingleItem>()
            requests.iterator().forEach { deferred ->
                list.add(deferred.await())
            }
            response.list = list
            callback.invoke(response)
        }
    }
}

fun getStnList(): ArrayList<String> {
    val stationList = ArrayList<String>()
    stationList.add("CSTM")
    stationList.add("LTT")
    stationList.add("PA")
    stationList.add("NGP")
    stationList.add("DR")
    stationList.add("KYN")
    stationList.add("TNA")
    stationList.add("SUR")
    stationList.add("VSKP")
    stationList.add("BBS")
    stationList.add("PURI")
    stationList.add("DBG")
    stationList.add("GYA")
    stationList.add("DHN")
    stationList.add("MFP")
    stationList.add("PNBE")
    stationList.add("BGP")
    stationList.add("HWH")
    stationList.add("SDAH")
    stationList.add("ALD")
    stationList.add("CNB")
    stationList.add("JHS")
    stationList.add("GWL")
    stationList.add("ABR")
    return stationList

}

