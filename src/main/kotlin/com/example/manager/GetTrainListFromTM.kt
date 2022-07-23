package com.example.manager

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import org.json.JSONArray
import org.json.JSONObject


class GetTrainListFromTM {

    fun getTrainsFromTrainMan(origin: String, dest: String, tmDate: String, doesRunIndex: Int): ArrayList<String> {
        //System.out.println("TM : " + date);
        val tmResponseList = ArrayList<String>()
        return try {
            RestAssured.baseURI = "https://www.trainman.in"
            val response: String =
                given().queryParam("key", "012562ae-60a9-4fcd-84d6-f1354ee1ea48").queryParam("sort", "smart")
                    .queryParam("quota", "GN").queryParam("meta", "true").queryParam("class", "ALL")
                    .queryParam("date", tmDate)
                    .`when`().get("services/trains/$origin/$dest")
                    .then().extract().response().asString()
            val jsonObject = JSONObject(response)
            val trainsArray: JSONArray = jsonObject.getJSONArray("trains")
            for (i in 0 until trainsArray.length()) {
                val trainObject: JSONObject = trainsArray.getJSONObject(i)
                val tCode: String = trainObject.getString("tcode")
                val dayOperation: String = trainObject.getString("doo")
                val yN = dayOperation[doesRunIndex]
                val ocode: String = trainObject.getString("ocode")
                val dcode: String = trainObject.getString("dcode")
                if (yN == 'Y' && ocode == origin && dcode == dest) {
                    tmResponseList.add(tCode)
                }
            }
            tmResponseList
        } catch (e: Exception) {
            tmResponseList
        }
    }
}