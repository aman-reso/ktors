package com.example.formatter

import org.json.JSONArray
import org.json.JSONObject

object GetIrctcFormattedTrainList {
    fun formatApiResponseFromIRCTC(origin: String, dest: String,doesRun:String, response: String): ArrayList<String> {
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
        return trainListUnderIRCTC
    }
}