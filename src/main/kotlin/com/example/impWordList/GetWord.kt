package com.example.impWordList

import com.example.impWordList.GetWord.getWordList
import com.example.setup.getTrainListFromIRCTC

var baseUrl = "https://mnemonicdictionary.com/wordlist/GREwordlist/startingwith/"
var dict = arrayListOf<String>("A", "B", "C", "D", "E")

object GetWord {
    suspend fun getWordList(url: String) {

    }

}

suspend fun main() {
    dict.forEach { letter ->
        val combinedBaseUrl = "$baseUrl$letter?page=1"
        getWordList(combinedBaseUrl)
    }
}
//class="pagination pagination-sm flex-wrap"