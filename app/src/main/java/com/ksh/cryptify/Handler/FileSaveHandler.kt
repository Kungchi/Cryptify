package com.ksh.cryptify.Handler

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileSaveHandler {
    // 텍스트를 파일로 저장하는 함수
     fun saveFile(context : Context ,text: String) {
        val fileName = "${text}.txt"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10(Q) 이상에서는 MediaStore 사용
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "text/plain")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            if (uri != null) {
                try {
                    val outputStream: OutputStream? = resolver.openOutputStream(uri)
                    outputStream?.use { stream ->
                        stream.write(text.toByteArray())
                        Toast.makeText(context, "파일이 다운로드되었습니다: $fileName", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    Toast.makeText(context, "파일 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        } else {
            val externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(externalStorageDir, fileName)

            try {
                FileOutputStream(file).use { outputStream ->
                    outputStream.write(text.toByteArray())
                    Toast.makeText(context, "파일이 다운로드되었습니다: $fileName", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Toast.makeText(context, "파일 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    fun createImageFile(fragment: Fragment): File {
        // 파일 이름 생성
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = fragment.requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* 접두사 */
            ".jpg",               /* 접미사 */
            storageDir            /* 디렉토리 */
        )
    }
}