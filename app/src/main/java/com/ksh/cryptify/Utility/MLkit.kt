package com.ksh.cryptify.Utility

import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException
import androidx.fragment.app.Fragment

class MLkit {

    fun processImage(fragment: Fragment, uri: Uri, callback: (String?) -> Unit) {
        try {
            val image = InputImage.fromFilePath(fragment.requireContext(), uri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // 텍스트 인식 성공
                    val recognizedText = visionText.text
                    Log.d("텍스트 인식 성공", recognizedText)
                    callback(recognizedText) // 콜백을 통해 텍스트 반환
                }
                .addOnFailureListener { e ->
                    // 에러 처리
                    e.printStackTrace()
                    Toast.makeText(fragment.requireContext(), "텍스트 인식에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    callback(null) // 실패 시 null 반환
                }
        } catch (e: IOException) {
            e.printStackTrace()
            callback(null) // 예외 발생 시 null 반환
        }
    }
}
