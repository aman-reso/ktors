package com.example.manager

import com.example.utility.CommonUtils
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.*

object Controller {
    private val getPopularStations by lazy { GetPopularStations() }
    private val getTrainListFromIrctc by lazy { GetTrainListFromIrctc() }
    private val getTrainListFromTM by lazy { GetTrainListFromTM() }
    private val requests = ArrayList<Deferred<ArrayList<String>>>()
    private val responseFromIrctcServer: ArrayList<String> by lazy { ArrayList() }
    private val responseFromTrainmanServer: ArrayList<String> by lazy { ArrayList() }

    suspend fun start(call: ApplicationCall) {
        val popularStations = getPopularStations.getPopularStations()
        val maxLimit = 1
        for (index in 1..maxLimit) {
            val irctcDate: String = CommonUtils.dateFormatForIrctc(index)
            val tmDate: String = CommonUtils.dateFormatForTrainMan(index)
            val doesRun: String = CommonUtils.getDay()
            val doesRunIndex: Int = CommonUtils.doesRunWithTM()

            //compareTrainList.start("NDLS", "SVDK", irctcDate,tmDate,doesRun,doesRunIndex);
            for (i in popularStations.indices) {
                for (j in popularStations.indices) {
                    if (i != j) {
                        val origin: String = popularStations[i]
                        val dest: String = popularStations[j]
                        CoroutineScope(Dispatchers.IO).launch {
                            requests.add(async {
                                getTrainListFromIrctc.getTrainsFromIrctc(origin, dest, irctcDate, tmDate, doesRun)
                            })
                        }
                    }
                }
            }
            val x = requests.awaitAll()
            println(x)
        }
    }
}
//val responseFromTrainman = async {
//                                    getTrainListFromTM.getTrainsFromTrainMan(
//                                        origin,
//                                        dest,
//                                        tmDate,
//                                        doesRunIndex
//                                    )
//                                }.await()