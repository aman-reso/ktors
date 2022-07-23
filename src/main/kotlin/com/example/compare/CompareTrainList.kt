package com.example.compare

import com.example.manager.GetTrainListFromIrctc
import com.example.manager.GetTrainListFromTM
import java.util.function.Consumer
import java.util.stream.Collectors

class CompareTrainList {
    var irctc = GetTrainListFromIrctc()

    //GetTrainListFromTrainMan trainman = new GetTrainListFromTrainMan();
    var tm = GetTrainListFromTM()
    var trainsOnBothPlatform = ArrayList<String>()
    var notFoundTrainsOnIrctc = ArrayList<String>()
    var notFoundTrainsOnTrainMan = ArrayList<String>()

    fun compareList(first: ArrayList<String>, second: ArrayList<String>, type: String?, origin: String, dest: String) {
        val list = ArrayList<String>()
        first.forEach(Consumer { trainNumber: String ->
            if (!second.contains(trainNumber)) {
                list.add(trainNumber)
            } else {
                trainsOnBothPlatform.add(trainNumber)
            }
        })
        val stnCode = "$origin $dest"
        if (type == FIRST_IRCTC) {
            //System.out.println("Trains On Both Platform : "+stnCode+"->" + trainsOnBothPlatform);
            if (list.size > 0) {
                notFoundTrainsOnTrainMan.addAll(list)
                println("Trains not on TM : " + " " + stnCode + "->" + list + "size = " + list.size)
            }
        } else if (type == FIRST_TM) {
            if (list.size > 0) {
                notFoundTrainsOnIrctc.addAll(list)
                println("Trains not on IRCTC : " + "-->" + stnCode + " " + list + "size = " + list.size)
            }
        }
    }

    suspend fun start(origin: String, dest: String, irctcDate: String, tmDate: String, doesRun: String, doesRunIndex: Int) {
        irctc.getTrainsFromIrctc(origin, dest, irctcDate, tmDate, doesRun)
    }

    fun didGetIrctcResponse(
        irctcResponse: ArrayList<String>,
        origin: String,
        dest: String,
        tmDate: String,
        doesRunIndex: Int
    ) {
        println("Irctc List : $origin $dest-->$irctcResponse")
        val tmResponse = tm.getTrainsFromTrainMan(origin, dest, tmDate, doesRunIndex)
        println("TrainMan List : $origin $dest-->$tmResponse")
        compareList(irctcResponse, tmResponse, FIRST_IRCTC, origin, dest)
        compareList(tmResponse, irctcResponse, FIRST_TM, origin, dest)
    }

    fun finalListForTrainman(): ArrayList<String> {
        return notFoundTrainsOnTrainMan.stream().distinct().collect(Collectors.toList()) as ArrayList<String>
    }

    fun finalListForIrctc(): ArrayList<String> {
        return notFoundTrainsOnIrctc.stream().distinct().collect(Collectors.toList()) as ArrayList<String>
    }

    companion object {
        var FIRST_IRCTC = "IRCTC_AT_FIRST_PARAMETER"
        var FIRST_TM = "TM_AT_FIRST_PARAMETER"
    }
}