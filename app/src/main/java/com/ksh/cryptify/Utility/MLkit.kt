package com.ksh.cryptify.Utility

import android.graphics.Rect
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import java.io.IOException
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions

class MLkit {

    fun processImage(fragment: Fragment, uri: Uri, callback: (List<String>, List<Rect>) -> Unit) {
        try {
            val image = InputImage.fromFilePath(fragment.requireContext(), uri)
            val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val texts = mutableListOf<String>()
                    val boundingBoxes = mutableListOf<Rect>()

                    // 텍스트와 boundingBox 정보 추출
                    visionText.textBlocks.forEach { block ->
                        block.lines.forEach { line ->
                            texts.add(line.text)
                            line.boundingBox?.let { boundingBoxes.add(it) }
                        }
                    }

                    // 텍스트와 boundingBox를 각각 리스트로 반환
                    callback(texts, boundingBoxes)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    callback(emptyList(), emptyList()) // 실패 시 빈 리스트 반환
                }
        } catch (e: IOException) {
            e.printStackTrace()
            callback(emptyList(), emptyList()) // 예외 발생 시 빈 리스트 반환
        }
    }
}
