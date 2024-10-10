package com.ksh.cryptify.Utility

import android.content.Context
import android.util.Base64
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset

class Endecode {

    // Base64 인코딩
    fun encodeBase64(input: String): String {
        return Base64.encodeToString(input.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
    }

    // Base64 디코딩
    fun decodeBase64(input: String): String {
        return String(Base64.decode(input, Base64.DEFAULT), Charsets.UTF_8)
    }

    // URL 인코딩
    fun encodeUrl(input: String): String {
        return URLEncoder.encode(input, "UTF-8")
    }

    // URL 디코딩
    fun decodeUrl(input: String): String {
        return URLDecoder.decode(input, "UTF-8")
    }

    // Hexadecimal 인코딩
    fun encodeHex(input: String): String {
        return input.toByteArray(Charsets.UTF_8).joinToString("") {
            String.format("%02x", it)
        }
    }

    // Hexadecimal 디코딩
    fun decodeHex(input: String): String {
        val output = StringBuilder()
        for (i in input.indices step 2) {
            val hexByte = input.substring(i, i + 2)
            val decimal = hexByte.toInt(16)
            output.append(decimal.toChar())
        }
        return output.toString()
    }

    // ASCII 인코딩
    fun encodeAscii(input: String): String {
        return input.toByteArray(Charset.forName("US-ASCII")).joinToString(" ") {
            it.toInt().toString()
        }
    }

    // ASCII 디코딩
    fun decodeAscii(input: String): String {
        return input.split(" ").map {
            it.toInt().toChar()
        }.joinToString("")
    }

    // UTF-8 인코딩
    fun encodeUtf8(input: String): ByteArray {
        return input.toByteArray(Charsets.UTF_8)
    }

    // UTF-8 디코딩
    fun decodeUtf8(input: ByteArray): String {
        return String(input, Charsets.UTF_8)
    }

    fun processText(context: Context, inputText: String, isEncoding: Boolean): String {
        // 설정한 인코딩/디코딩 방식 가져오기
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val selectedMethod = sharedPreferences.getInt("encoding_method", 0)

        // 인코딩/디코딩 처리
        return if (isEncoding) {
            // 인코딩
            when (selectedMethod) {
                0 -> encodeBase64(inputText)   // Base64
                1 -> encodeUrl(inputText)      // URL 인코딩
                2 -> encodeHex(inputText)      // Hexadecimal
                3 -> encodeAscii(inputText)    // ASCII
                4 -> String(encodeUtf8(inputText)) // UTF-8
                else -> inputText  // 기본값
            }
        } else {
            // 디코딩
            when (selectedMethod) {
                0 -> decodeBase64(inputText)   // Base64
                1 -> decodeUrl(inputText)      // URL 디코딩
                2 -> decodeHex(inputText)      // Hexadecimal
                3 -> decodeAscii(inputText)    // ASCII
                4 -> decodeUtf8(inputText.toByteArray()) // UTF-8
                else -> inputText  // 기본값
            }
        }
    }
}
