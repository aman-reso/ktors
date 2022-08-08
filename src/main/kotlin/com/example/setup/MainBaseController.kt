package com.example.setup

import com.example.compare.CompareTrainList
import com.example.manager.SingleItem
import kotlinx.coroutines.*

class MainBaseController {
    private val compareTrainList: CompareTrainList by lazy { CompareTrainList() }
    suspend fun controlFunction(fromStnCode: String, toStnCode: String, irctcDate: String, tmDate: String, doesRun: String, doesRunIndex: Int) =
        coroutineScope {
            val responseFromIrctc = async { getTrainListFromIRCTC(fromStnCode, toStnCode, irctcDate, doesRun) }.await()
            val responseFromTm = async { getTrainListFromTrainman(fromStnCode, toStnCode, tmDate, doesRunIndex) }.await()
            val key = "$fromStnCode-$toStnCode"
            val customResponse = compareTrainList.compareListCombine(responseFromIrctc, responseFromTm)
            return@coroutineScope SingleItem(key, customResponse.tmList!!, customResponse.irctcList!!, responseFromIrctc, responseFromTm)
        }
}


suspend fun main() {
    System.out.println(getStnList().size)
    System.out.println(GetTrainStationCode.getAllStationCode().subList(0,20))
}

