package com.example.setup

import com.example.formatter.GetIrctcFormattedTrainList
import com.example.formatter.GetTrainmanFormattedTrainList
import com.example.utility.CommonUtils
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import org.json.JSONArray
import org.json.JSONObject

val ktorHttpClient = HttpClient(CIO) {
    install(HttpTimeout) {
        requestTimeoutMillis = 1200000
        connectTimeoutMillis = 120000
        socketTimeoutMillis = 120000
    }
}
val client = HttpClient(CIO) {
    engine {
        requestTimeout = 0
        maxConnectionsCount = 100000
        // 0 to disable, or a millisecond value to fit your needs
    }
}

const val BASE_URL_IRCTC = "https://www.irctc.co.in"
const val PATH_IRCTC = "/eticketing/protected/mapps1/altAvlEnq/TC"
const val BASE_URL_TM = "https://www.trainman.in"
const val PATH_TM = ""
suspend fun getTrainListFromIRCTC(origin: String, dest: String, irctcDate: String, doesRun: String): ArrayList<String> {
    val key = "$origin--$dest"
    val httpResponse = ktorHttpClient.post(BASE_URL_IRCTC + PATH_IRCTC) {
        header("greq", System.currentTimeMillis())
        header("Content-Type", "application/json")
        setBody(CommonUtils.getTrainListIRCTC(origin, dest, irctcDate))
    }
    return try {
        val response = httpResponse.body<String>()
        return GetIrctcFormattedTrainList.formatApiResponseFromIRCTC(origin, dest, doesRun, response)
    } catch (e: java.lang.Exception) {
        println(key + "--" + e.localizedMessage)
        ArrayList()
    }

}

suspend fun getTrainListFromTrainman(origin: String, dest: String, tmDate: String, index: Int): ArrayList<String> {
    val key = "$origin--$dest"
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
    return try {
        val response = httpResponse.body<String>()
        GetTrainmanFormattedTrainList.formattedTrainsFromTrainMan(origin, dest, index, response)

    } catch (e: java.lang.Exception) {
        println(key + "--" + e.localizedMessage)
        ArrayList()
    }
}

