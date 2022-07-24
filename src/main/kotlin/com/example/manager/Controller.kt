package com.example.manager

import com.example.compare.CompareTrainList
import com.example.compare.CustomResponse
import com.example.utility.CommonUtils
import kotlinx.coroutines.*

object Controller {
    private val getPopularStations by lazy { GetPopularStations() }
    private val getTrainListFromIrctc by lazy { GetTrainListFromIrctc() }
    private val getTrainListFromTM by lazy { GetTrainListFromTM() }
    private var requests = ArrayList<Deferred<HashMap<String, CustomResponse>>>()
    private var maxLimit = 1
    private val compareTrainList by lazy { CompareTrainList() }
    suspend fun start(commandKey: Int, oCode: String?, dCode: String?, dateLimit: Int, callback: suspend (Response) -> Unit) {
        requests = ArrayList()
        coroutineScope {
            val popularStations = getPopularStations.getPopularStations()
            System.out.println(popularStations)
            async {
                maxLimit = dateLimit
                startGettingResponseFromIrctc(commandKey, oCode, dCode, popularStations, callback)
            }
        }
    }

    private suspend fun startGettingResponseFromIrctc(commandKey: Int, oCode: String?, dCode: String?, popularStations: ArrayList<String>, callback: suspend (Response) -> Unit) {
        for (index in 1..maxLimit) {
            val irctcDate: String = CommonUtils.dateFormatForIrctc(index)
            val tmDate: String = CommonUtils.dateFormatForTrainMan(index)
            val doesRun: String = CommonUtils.getDay()
            val doesRunIndex: Int = CommonUtils.doesRunWithTM()
            if (commandKey == 1) {
                if (oCode != null && dCode != null) {
                    println("ocode-->$oCode")
                    println("dCode-->$dCode")
                    executeSingleResponse(oCode, dCode, irctcDate, tmDate, doesRun, doesRunIndex, callback)
                } else {
                    executeSingleResponse("NDLS", "MMCT", irctcDate, tmDate, doesRun, doesRunIndex, callback)
                }
            } else {
                for (i in popularStations.indices) {
                    for (j in popularStations.indices) {
                        if (i != j) {
                            val origin: String = popularStations[i]
                            val dest: String = popularStations[j]
                            CoroutineScope(Dispatchers.IO).launch {
                                val irctcListOfTrains = async { getTrainListFromIrctc.getTrainsFromIrctc(origin, dest, irctcDate, tmDate, doesRun) }
                                val tmListOfTrains = async { getTrainListFromTM.getTrainsFromTrainMan(origin, dest, tmDate, doesRunIndex) }
                                val key = "$origin--$dest"
                                requests.add(async {
                                    val irctcList = irctcListOfTrains.await()
                                    val tmList = tmListOfTrains.await()
                                    val map = HashMap<String, CustomResponse>()
                                    val customResponse = CustomResponse()
                                    customResponse.tmList = tmList
                                    customResponse.irctcList = irctcList
                                    map[key] = customResponse
                                    map
                                })
                            }
                        }
                    }
                }
                val x = requests.awaitAll()
                val singleItems = ArrayList<SingleItem>()
                x.forEach {
                    it.forEach { map ->
                        val key = map.key
                        val customResponse = map.value
                        if (customResponse.irctcList != null && customResponse.tmList != null) {
                            val trainNotOnTm = compareTrainList.compareList(customResponse.irctcList!!, customResponse.tmList!!, CompareTrainList.FIRST_IRCTC, key)
                            val trainNotOnIrctc = compareTrainList.compareList(customResponse.tmList!!, customResponse.irctcList!!, CompareTrainList.FIRST_TM, key)
                            singleItems.add(SingleItem(key, trainNotOnTm, trainNotOnIrctc, customResponse.irctcList, customResponse.tmList))
                        }
                    }
                }
                println("singleItems-->$singleItems")
                callback.invoke(Response(singleItems, tmDate))
            }

        }

    }

    private suspend fun executeSingleResponse(origin: String, dest: String, irctcDate: String, tmDate: String, doesRun: String, doesRunIndex: Int, callback: suspend (Response) -> Unit) {
        System.out.println("tmDate-->$tmDate")
        CoroutineScope(Dispatchers.IO).launch {
            val irctcListOfTrains = async { getTrainListFromIrctc.getTrainsFromIrctc(origin, dest, irctcDate, tmDate, doesRun) }
            val tmListOfTrains = async { getTrainListFromTM.getTrainsFromTrainMan(origin, dest, tmDate, doesRunIndex) }
            val customResponse = CustomResponse()
            customResponse.irctcList = irctcListOfTrains.await()
            customResponse.tmList = tmListOfTrains.await()
            val key = "$origin--$dest"
            println("irctcList ${irctcListOfTrains.await()}")
            println("tmList ${tmListOfTrains.await()}")
            val trainNotOnTm = compareTrainList.compareList(customResponse.irctcList!!, customResponse.tmList!!, CompareTrainList.FIRST_IRCTC, key)
            val trainNotOnIrctc = compareTrainList.compareList(customResponse.tmList!!, customResponse.irctcList!!, CompareTrainList.FIRST_TM, key)
            val singleItem = SingleItem(key, trainNotOnTm, trainNotOnIrctc, customResponse.irctcList, customResponse.tmList)
            val items = ArrayList<SingleItem>()
            items.add(singleItem)
            println(items)
            val response = Response(items, tmDate)
            callback.invoke(response)
        }

    }

}

data class Response(var list: ArrayList<SingleItem>, var date: String? = "")
data class SingleItem(var key: String, var trainNotOnTm: ArrayList<String>, var trainNotOnIrctc: ArrayList<String>,
                      var irctcTrains: ArrayList<String>? = ArrayList(), var tmTrains: ArrayList<String>? = ArrayList())