package com.example.manager

import com.example.setup.getStnList
import io.restassured.RestAssured
import org.json.JSONObject

class GetPopularStations {
    suspend fun getPopularStations(): ArrayList<String> {
        val stationListV2 = ArrayList<String>()
        RestAssured.baseURI = "https://dev.flightman.in"
        val response = RestAssured.given()
            .`when`()["/services/get-popular-stations"]
            .then().assertThat().statusCode(200)
            .extract().response().asString()
        try {
            val js = JSONObject(response)
            val stationList = js.getJSONArray("data")
            for (i in 0..stationList.length()) {
                val station = stationList.getJSONObject(i)
                val trainCode = station.getString("code")
                stationListV2.add(trainCode)
            }
        } catch (e: Exception) {

        }
        for (e in getStnList()) {
//            stationListV2.add(e)
        }

        return stationListV2
    }
}