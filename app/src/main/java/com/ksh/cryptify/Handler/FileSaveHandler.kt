package com.ksh.cryptify.Handler

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ksh.cryptify.Utility.Endecode
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileSaveHandler {

    val REQUEST_FILE_GET = 1

    fun FilePicker(fragment: Fragment) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "text/plain"
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        // resolveActivity로 실행할 수 있는 앱이 있는지 확인
        if (intent.resolveActivity(fragment.requireContext().packageManager) != null) {
            // Intent를 시작
            fragment.startActivityForResult(intent, REQUEST_FILE_GET)
        } else {
            // 파일을 처리할 앱이 없을 경우 처리
            Toast.makeText(fragment.requireContext(), "파일을 선택할 수 있는 앱이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 텍스트를 파일로 저장하는 함수
    fun saveFile(context: Context, fileName: String, text: String) {
        val completeFileName = "$fileName.txt"
        val folderName = "Cryptify"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10(Q) 이상에서는 MediaStore 사용
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, completeFileName)
                put(MediaStore.Downloads.MIME_TYPE, "text/plain")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + File.separator + folderName)
            }

            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            if (uri != null) {
                try {
                    val outputStream: OutputStream? = resolver.openOutputStream(uri)
                    outputStream?.use { stream ->
                        stream.write(text.toByteArray())
                        Toast.makeText(context, "파일이 다운로드되었습니다: $completeFileName", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    Toast.makeText(context, "파일 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        } else {
            // Android 9 이하에서는 직접 경로를 사용해 파일을 저장
            val externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val cryptifyDir = File(externalStorageDir, folderName)

            // Cryptify 폴더가 없으면 생성
            if (!cryptifyDir.exists()) {
                cryptifyDir.mkdirs()
            }

            val file = File(cryptifyDir, completeFileName)

            try {
                FileOutputStream(file).use { outputStream ->
                    outputStream.write(text.toByteArray())
                    Toast.makeText(context, "파일이 다운로드되었습니다: $completeFileName", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Toast.makeText(context, "파일 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }


    // 파일을 Base64로 인코딩하고 저장하는 함수
    fun encodeFile(context: Context, fileUri: Uri, fileName: String) {
        // 파일 내용 읽기
        val fileContent = readFile(context, fileUri)

        fileContent?.let { content ->
            // 인코딩 처리
            val encodedContent = Endecode().processText(context, content, true)

            // 파일 저장 (파일 이름에 "_Encode" 추가)
            val newFileName = "${fileName}_Encode.txt"
            FileSaveHandler().saveFile(context, newFileName, encodedContent)

            Toast.makeText(context, "파일이 인코딩되어 저장되었습니다: $newFileName", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, "파일을 읽는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }


    // 파일을 Base64로 디코딩하고 저장하는 함수
    fun decodeFile(context: Context, fileUri: Uri, fileName: String) {
        // 파일 내용 읽기
        val fileContent = readFile(context, fileUri)

        fileContent?.let { content ->
            // 디코딩 처리
            val decodedContent = Endecode().processText(context, content, false)

            // 파일 저장 (파일 이름에 "_Decode" 추가)
            val newFileName = "${fileName}_Decode.txt"
            FileSaveHandler().saveFile(context, newFileName, decodedContent)

            Toast.makeText(context, "파일이 디코딩되어 저장되었습니다: $newFileName", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, "파일을 읽는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun readFile(context: Context, fileUri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(fileUri)
            inputStream?.bufferedReader()?.use(BufferedReader::readText)
        } catch (e: Exception) {
            e.printStackTrace()
            null
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