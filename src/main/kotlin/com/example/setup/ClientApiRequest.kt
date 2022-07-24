package com.example.setup

import com.example.formatter.GetIrctcFormattedTrainList
import com.example.formatter.GetTrainmanFormattedTrainList
import com.example.utility.CommonUtils
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*

val ktorHttpClient = HttpClient(CIO) {

}

const val BASE_URL_IRCTC = "https://www.irctc.co.in"
const val PATH_IRCTC = "/eticketing/protected/mapps1/altAvlEnq/TC"
const val BASE_URL_TM = "https://www.trainman.in"
const val PATH_TM = ""
suspend fun getTrainListFromIRCTC(origin: String, dest: String, irctcDate: String, doesRun: String): ArrayList<String> {
    val httpResponse = ktorHttpClient.post(BASE_URL_IRCTC + PATH_IRCTC) {
        header("greq", System.currentTimeMillis())
        header("Content-Type", "application/json")
        setBody(CommonUtils.getTrainListIRCTC(origin, dest, irctcDate))
    }
    val response = httpResponse.body<String>()
    return GetIrctcFormattedTrainList.formatApiResponseFromIRCTC(origin, dest, doesRun, response)
}

suspend fun getTrainListFromTrainman(origin: String, dest: String, tmDate: String, index: Int): ArrayList<String> {
    val httpResponse = ktorHttpClient.get("$BASE_URL_TM/services/trains/$origin/$dest") {
        header("greq", System.currentTimeMillis())
        header("Content-Type", "application/json")
        parameter("key", "012562ae-60a9-4fcd-84d6-f1354ee1ea48")
        parameter("sort", "smart")
        parameter("quota", "GN")
        parameter("meta", "true")
        parameter("class", "ALL")
        parameter("date", tmDate)
    }
    val response = httpResponse.body<String>()
    return GetTrainmanFormattedTrainList.formattedTrainsFromTrainMan(origin, dest, index, response)
}
//given().queryParam("key", "012562ae-60a9-4fcd-84d6-f1354ee1ea48").queryParam("sort", "smart")
//                    .queryParam("quota", "GN").queryParam("meta", "true").queryParam("class", "ALL")
//                    .queryParam("date", tmDate)
//                    .`when`().get("services/trains/$origin/$dest")