package com.example.manager

import com.example.utility.CommonUtils
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import org.json.JSONArray
import org.json.JSONObject

class GetTrainListFromIrctc {
    private var currentTimeStamp = System.currentTimeMillis()
    private val path = "/eticketing/protected/mapps1/altAvlEnq/TC"
    suspend fun getTrainsFromIrctc(origin: String, dest: String, irctcDate: String, tmDate: String, doesRun: String): ArrayList<String> {
        RestAssured.baseURI = "https://www.irctc.co.in"
        val response: String = given().header("greq", currentTimeStamp).header("Content-Type", "application/json")
            .body(CommonUtils.getTrainListIRCTC(origin, dest, irctcDate))
            .`when`().post(path)
            .then().extract().response().asString()

        val trainListUnderIRCTC = ArrayList<String>()
        try {
            val jsonObject = JSONObject(response)
            if (jsonObject.has("trainBtwnStnsList")) {
                when (jsonObject.get("trainBtwnStnsList")) {
                    is JSONObject -> {
                        val trainListObject: JSONObject = jsonObject.getJSONObject("trainBtwnStnsList")
                        val doesRunToday: String = trainListObject.getString(doesRun)
                        val trainNumber: String = trainListObject.getString("trainNumber")
                        val fromStnCode: String = trainListObject.getString("fromStnCode")
                        val toStnCode: String = trainListObject.getString("toStnCode")
                        if (doesRunToday == "Y" && fromStnCode == origin && toStnCode == dest) {
                            trainListUnderIRCTC.add(trainNumber)
                        }
                    }
                    is JSONArray -> {
                        val trainList: JSONArray = jsonObject.getJSONArray("trainBtwnStnsList")
                        for (i in 0 until trainList.length()) {
                            val doesRunToday: String = trainList.getJSONObject(i).getString(doesRun)
                            val fromStnCode: String = trainList.getJSONObject(i).getString("fromStnCode")
                            val toStnCode: String = trainList.getJSONObject(i).getString("toStnCode")
                            if (doesRunToday == "Y" && fromStnCode == origin && toStnCode == dest) {
                                val trainNumber: String = trainList.getJSONObject(i).getString("trainNumber")
                                trainListUnderIRCTC.add(trainNumber)
                            }
                        }
                    }

                }
            }
        } catch (_: Exception) {

        }
        System.out.println("irctcTrains-->$trainListUnderIRCTC")
        return trainListUnderIRCTC
        //trainListUnderIRCTC, origin, dest, tmDate, doesRunIndex;
    }
}