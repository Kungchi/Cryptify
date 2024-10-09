package com.ksh.cryptify.Handler

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.io.IOException

class CameraHandler {
    val REQUEST_IMAGE_CAPTURE = 1
    var photoURI: Uri? = null

    fun dispatchTakePictureIntent(fragment: Fragment) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(fragment.requireActivity().packageManager) != null) {
            val photoFile: File? = try {
                FileSaveHandler().createImageFile(fragment)
            } catch (ex: IOException) {
                // 에러 처리
                null
            }
            photoFile?.also {
                photoURI = FileProvider.getUriForFile(
                    fragment.requireContext(),
                    "com.ksh.cryptify.fileprovider", // 패키지명에 맞게 변경하세요.
                    it
                )
                Log.d("카메라 핸들러 확인", photoURI.toString())
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                fragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }
}