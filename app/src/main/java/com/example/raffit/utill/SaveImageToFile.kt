package com.example.raffit.utill

import android.app.Activity
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class SaveImageToFile(private val activity: Activity) {
    suspend operator fun invoke(imageUrl: String) = withContext(Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            Toast.makeText(activity, "다운로드를 시작합니다.", Toast.LENGTH_SHORT).show()
        }
        val url = URL(imageUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()

        val input: InputStream = connection.inputStream
        val bitmap = BitmapFactory.decodeStream(input)

        val contentResolver = activity.contentResolver

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "PNG_" + System.currentTimeMillis())
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        return@withContext runCatching {
            contentResolver.openFileDescriptor(imageUri!!, "w")?.use { pfd ->
                FileOutputStream(pfd.fileDescriptor).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }

            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(imageUri, values, null, null)

            withContext(Dispatchers.Main) {
                Toast.makeText(activity, "다운로드를 완료했습니다.", Toast.LENGTH_SHORT).show()
            }
        }.onFailure {
            Log.e("SaveImageToFile", "Error fetching data: $it")
        }
    }
}