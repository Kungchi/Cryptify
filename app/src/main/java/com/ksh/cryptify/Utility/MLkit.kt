package com.ksh.cryptify.Utility

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import java.io.IOException
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class MLkit {

    fun processImage(fragment: Fragment, uri: Uri, callback: (List<String>, List<Rect>) -> Unit) {

        val sharedPreferences = fragment.requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        var savedLanguage = sharedPreferences.getString("language", null)

        try {
            val image = InputImage.fromFilePath(fragment.requireContext(), uri)
            val recognizer = when(savedLanguage) {
                "en" -> TextRecognition.getClient(TextRecognizerOptions.Builder().build()) // 영어 MLKIT
                "zh" -> TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build()) // 중국어 MLKIT
                "hi" -> TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build()) // 힌디어 MLKIT
                "ja" -> TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build()) // 일본어 MLKIT
                "ko" -> TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build()) // 한국어 MLKIT
                else -> TextRecognition.getClient(TextRecognizerOptions.Builder().build()) // 기본값: 영어 MLKIT
            }
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
