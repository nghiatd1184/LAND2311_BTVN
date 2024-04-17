package com.nghiatd.bt3.model

import org.json.JSONArray
import org.json.JSONObject

data class File(
    val title : String,
    val body : String
) {
    override fun toString(): String {
        return "{\"title\" : $title,\"body\" : $body}"
    }
}

fun String.convertStringToFileModel() :File {
    val json = JSONObject(this)
    return File(
        title = json.getString("title"),
        body = json.getString("body")
    )
}