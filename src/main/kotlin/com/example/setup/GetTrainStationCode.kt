package com.example.setup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.text.ParseException
import java.util.function.Consumer


object GetTrainStationCode {
    suspend fun getAllStationCode(): ArrayList<String> {
        val list = ArrayList<String>()
        val bufferedReader: BufferedReader = File("stations.json").bufferedReader()
        val inputString = bufferedReader.use { it.readText() }
        println(inputString)
        val jsonObject = JSONObject(inputString)
        val dataArray = jsonObject.getJSONArray("data")
        for (i in 0 until dataArray.length()) {
            val obj = dataArray.getJSONObject(i)
            val stnCode = obj.getString("code")
            list.add(stnCode)
        }
        return list
    }
}