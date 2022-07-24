package com.example.setup

import com.example.manager.Controller
import com.example.utility.CommonUtils
import kotlinx.coroutines.*

class MainBaseController {
    suspend fun controlFunction(fromStnCode: String, toStnCode: String, callback: suspend (List<String>) -> Unit) {
        for (index in 1..Controller.maxLimit) {
            val irctcDate: String = CommonUtils.dateFormatForIrctc(index)
            val tmDate: String = CommonUtils.dateFormatForTrainMan(index)
            val doesRun: String = CommonUtils.getDay()
            val doesRunIndex: Int = CommonUtils.doesRunWithTM()
            try {
                supervisorScope {
                    val responseFromIrctc = async { getTrainListFromIRCTC(fromStnCode, toStnCode, irctcDate, doesRun) }.await()
                    val responseFromTm = async { getTrainListFromTrainman(fromStnCode, toStnCode, tmDate, doesRunIndex) }.await()
                    val x = responseFromTm + responseFromIrctc
                    callback.invoke(x)
                }
            }catch (e:Exception){
                callback.invoke(ArrayList())
            }
        }
    }
}


suspend fun main() {

}

