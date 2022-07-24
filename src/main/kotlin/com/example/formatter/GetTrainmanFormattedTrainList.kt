package com.example.formatter

import io.restassured.RestAssured
import org.json.JSONArray
import org.json.JSONObject

object GetTrainmanFormattedTrainList {
    fun formattedTrainsFromTrainMan(origin: String, dest: String, doesRunIndex: Int,response:String): ArrayList<String> {
        val tmResponseList = ArrayList<String>()
        return try {
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