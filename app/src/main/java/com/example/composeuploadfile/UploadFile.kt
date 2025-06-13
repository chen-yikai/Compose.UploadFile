package com.example.composeuploadfile

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files

const val host = "https://skills-upload-file.eliaschen.dev"

suspend fun uploadFile(file: File): String {
    return withContext(Dispatchers.IO) {
        val boundary = "----${System.currentTimeMillis()}----"
        val lineEnd = "\r\n"
        val twoHyphens = "--"

        val url = URL("$host/api/upload")
        val conn = url.openConnection() as HttpURLConnection
        conn.apply {
            doOutput = true
            requestMethod = "POST"
            setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
        }

        try {
            conn.outputStream.use { output ->
                output.write("$twoHyphens$boundary$lineEnd".toByteArray())
                output.write(
                    """Content-Disposition: form-data; name="file"; filename="${file.name}"$lineEnd""".toByteArray()
                )
                output.write("Content-Type: ${Files.probeContentType(file.toPath())}$lineEnd$lineEnd".toByteArray())
                file.inputStream().use { input ->
                    input.copyTo(output)
                }
                output.write("$lineEnd$twoHyphens$boundary$twoHyphens$lineEnd".toByteArray())
            }

            return@withContext conn.inputStream.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            Log.e("uploadFile", e.toString())
            return@withContext "Error: ${e.message}"
        }
    }
}