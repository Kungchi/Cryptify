package com.ksh.cryptify.Handler

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import com.ksh.cryptify.R
import com.ksh.cryptify.Utility.CustomToast
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

        if (intent.resolveActivity(fragment.requireContext().packageManager) != null) {
            fragment.startActivityForResult(intent, REQUEST_FILE_GET)
        } else {
            CustomToast.show(fragment.requireContext(), fragment.getString(R.string.toast_no_file_app))
        }
    }

    fun saveFile(context: Context, fileName: String, text: String, toast: Boolean = true) {
        val completeFileName = "$fileName.txt"
        val folderName = "Cryptify"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
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
                        if (toast) {
                            CustomToast.show(context, context.getString(R.string.toast_file_saved, completeFileName))
                        }
                    }
                } catch (e: IOException) {
                    CustomToast.show(context, context.getString(R.string.toast_file_save_error))
                    e.printStackTrace()
                }
            }
        } else {
            val externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val cryptifyDir = File(externalStorageDir, folderName)

            if (!cryptifyDir.exists()) {
                cryptifyDir.mkdirs()
            }

            val file = File(cryptifyDir, completeFileName)

            try {
                FileOutputStream(file).use { outputStream ->
                    outputStream.write(text.toByteArray())
                    if (toast) {
                        CustomToast.show(context, context.getString(R.string.toast_file_saved, completeFileName))
                    }
                }
            } catch (e: IOException) {
                CustomToast.show(context, context.getString(R.string.toast_file_save_error))
                e.printStackTrace()
            }
        }
    }

    fun encodeFile(context: Context, fileUri: Uri, fileName: String) {
        val fileContent = readFile(context, fileUri)

        fileContent?.let { content ->
            val encodedContent = Endecode().processText(context, content, true)
            val newFileName = "${fileName}_Encode.txt"
            saveFile(context, newFileName, encodedContent, toast = false)
            CustomToast.show(context, context.getString(R.string.toast_file_encode_saved, newFileName))
        } ?: run {
            CustomToast.show(context, context.getString(R.string.toast_file_read_error))
        }
    }

    fun decodeFile(context: Context, fileUri: Uri, fileName: String) {
        val fileContent = readFile(context, fileUri)

        fileContent?.let { content ->
            val decodedContent = Endecode().processText(context, content, false)
            val newFileName = "${fileName}_Decode.txt"
            saveFile(context, newFileName, decodedContent, toast = false)
            CustomToast.show(context, context.getString(R.string.toast_file_decode_saved, newFileName))
        } ?: run {
            CustomToast.show(context, context.getString(R.string.toast_file_read_error))
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
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = fragment.requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* 접두사 */
            ".jpg",               /* 접미사 */
            storageDir            /* 디렉토리 */
        )
    }
}