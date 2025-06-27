package com.example.composeuploadfile

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class Files(
    val name: String,
    val url: String
)

fun getFiles(): Flow<List<Files>> = flow {
    val files: List<Files> = withContext(Dispatchers.IO) {
        val url = URL("$host/api/files").openConnection() as HttpURLConnection
        url.requestMethod = "GET"
        val jsonText = url.inputStream.bufferedReader().use { it.readText() }
        val jsonFilesObj = JSONObject(jsonText).getJSONArray("files")
        return@withContext List(jsonFilesObj.length()) {
            val file = jsonFilesObj.getJSONObject(it)
            Files(
                file.getString("name"),
                file.getString("url")
            )
        }
    }
    emit(files)
}.catch {
    Log.e("getFiles", it.toString())
    emit(emptyList())
}