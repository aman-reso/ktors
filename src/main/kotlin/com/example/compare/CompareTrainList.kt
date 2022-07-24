package com.example.compare

import com.example.manager.GetTrainListFromIrctc
import com.example.manager.GetTrainListFromTM
import kotlinx.coroutines.*
import java.util.function.Consumer

class CompareTrainList {
    private val getTrainListFromIrctc by lazy { GetTrainListFromIrctc() }
    private val getTrainListFromTM by lazy { GetTrainListFromTM() }
    private var trainsOnBothPlatform = ArrayList<String>()
    private var notFoundTrainsOnIrctc = ArrayList<String>()
    private var notFoundTrainsOnTrainMan = ArrayList<String>()

    open suspend fun compareList(
        first: ArrayList<String>,
        second: ArrayList<String>,
        type: String?,key:String
    ): ArrayList<String> {
        val list = ArrayList<String>()
        first.forEach(Consumer { trainNumber: String ->
            if (!second.contains(trainNumber)) {
                list.add(trainNumber)
            } else {
                trainsOnBothPlatform.add(trainNumber)
            }
        })
        if (type == FIRST_IRCTC) {
            if (list.size > 0) {
                println("Trains not on TM : " + " " + key + "->" + list + "size = " + list.size)
            }
        } else if (type == FIRST_TM) {
            if (list.size > 0) {
                println("Trains not on IRCTC : " + "-->" + key + " " + list + "size = " + list.size)
            }
        }
        return list
    }

    suspend fun startComputing(
        origin: String,
        dest: String,
        irctcDate: String,
        tmDate: String,
        doesRun: String,
        doesRunIndex: Int
    ) {
        val customResponse = CustomResponse()
        CoroutineScope(Dispatchers.IO).launch {
            val irctcListOfTrains = async { getTrainListFromIrctc.getTrainsFromIrctc(origin, dest, irctcDate, tmDate, doesRun) }
            val tmListOfTrains = async { getTrainListFromTM.getTrainsFromTrainMan(origin, dest, tmDate, doesRunIndex) }
            print("${irctcListOfTrains.await() + "," + tmListOfTrains.await()}")
        }
    }

    open suspend fun compareListCombine(
        first: ArrayList<String>,
        second: ArrayList<String>,
    ): CustomResponse {
        val list = ArrayList<String>()
        first.forEach(Consumer { trainNumber: String ->
            if (!second.contains(trainNumber)) {
                list.add(trainNumber)
            }
        })
        val list1 = ArrayList<String>()
        second.forEach(Consumer { trainNumber: String ->
            if (!first.contains(trainNumber)) {
                list1.add(trainNumber)
            }
        })
        val customResponse = CustomResponse()
        customResponse.irctcList = list
        customResponse.irctcList = list1
        return customResponse
    }

    companion object {
        var FIRST_IRCTC = "IRCTC_AT_FIRST_PARAMETER"
        var FIRST_TM = "TM_AT_FIRST_PARAMETER"
    }
}

data class CustomResponse(
    var irctcList: ArrayList<String>? = ArrayList(),
    var tmList: ArrayList<String>? = ArrayList()
)