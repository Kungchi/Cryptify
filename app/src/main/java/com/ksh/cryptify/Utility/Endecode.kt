package com.ksh.cryptify.Utility

import android.content.Context
import com.ksh.cryptify.R
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.net.URLCodec
import java.net.IDN
import java.nio.charset.StandardCharsets

class Endecode {

    // Base64 인코딩 (Apache Commons Codec 사용)
    fun encodeBase64(input: String): String {
        return String(Base64.encodeBase64(input.toByteArray(StandardCharsets.UTF_8)))
    }

    // Base64 디코딩 (Apache Commons Codec 사용)
    fun decodeBase64(input: String): String {
        return String(Base64.decodeBase64(input.toByteArray(StandardCharsets.UTF_8)))
    }

    // URL 인코딩 (Apache Commons Codec 사용)
    fun encodeUrl(input: String): String {
        val codec = URLCodec()
        return codec.encode(input)
    }

    // URL 디코딩 (Apache Commons Codec 사용)
    fun decodeUrl(input: String): String {
        val codec = URLCodec()
        return codec.decode(input)
    }

    // Hexadecimal 인코딩 (Apache Commons Codec 사용)
    fun encodeHex(input: String): String {
        return String(Hex.encodeHex(input.toByteArray(StandardCharsets.UTF_8)))
    }

    // Hexadecimal 디코딩 (Apache Commons Codec 사용)
    fun decodeHex(input: String): String {
        return String(Hex.decodeHex(input))
    }

    // ASCII 인코딩
    fun encodeAscii(input: String): String {
        return input.toByteArray(StandardCharsets.US_ASCII).joinToString(" ") {
            it.toInt().toString()
        }
    }

    // ASCII 디코딩
    fun decodeAscii(input: String): String {
        return input.split(" ").map { it.toInt().toChar() }.joinToString("")
    }

    // UTF-8 인코딩
    fun encodeUtf8(input: String): ByteArray {
        return input.toByteArray(StandardCharsets.UTF_8)
    }

    // UTF-8 디코딩
    fun decodeUtf8(input: ByteArray): String {
        return String(input, StandardCharsets.UTF_8)
    }

    fun encodeBinary(input: String): String {
        return input.toByteArray(Charsets.UTF_8).joinToString(" ") {
            it.toUByte().toString(2).padStart(8, '0')  // 각 바이트를 8비트로 표현
        }
    }

    // Binary 디코딩
    fun decodeBinary(input: String): String {
        return input.split(" ").map {
            it.toInt(2).toByte()  // 각 2진수를 바이트로 변환
        }.toByteArray().toString(Charsets.UTF_8)  // UTF-8 문자열로 변환
    }

    // ROT13 인코딩/디코딩 (ROT13은 동일한 방식으로 인코딩 및 디코딩)
    fun encodeDecodeRot13(input: String): String {
        return input.map {
            when (it) {
                in 'A'..'Z' -> 'A' + (it - 'A' + 13) % 26
                in 'a'..'z' -> 'a' + (it - 'a' + 13) % 26
                else -> it
            }
        }.joinToString("")
    }

    // Quoted-Printable 인코딩 (Apache Commons Codec 사용)
    fun encodeQuotedPrintable(input: String): String {
        val codec = org.apache.commons.codec.net.QuotedPrintableCodec()
        return codec.encode(input)
    }

    // Quoted-Printable 디코딩 (Apache Commons Codec 사용)
    fun decodeQuotedPrintable(input: String): String {
        val codec = org.apache.commons.codec.net.QuotedPrintableCodec()
        return codec.decode(input)
    }

    // Punycode 인코딩
    fun encodePunycode(input: String): String {
        return IDN.toASCII(input)
    }

    // Punycode 디코딩
    fun decodePunycode(input: String): String {
        return IDN.toUnicode(input)
    }

    fun processText(context: Context, inputText: String, isEncoding: Boolean): String {
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val selectedMethod = sharedPreferences.getInt("encoding_method", 0)

        return try {
            if (isEncoding) {
                when (selectedMethod) {
                    0 -> encodeBase64(inputText)
                    1 -> encodeUrl(inputText)
                    2 -> encodeHex(inputText)
                    3 -> encodeAscii(inputText)
                    4 -> String(encodeUtf8(inputText))
                    5 -> encodeBinary(inputText)
                    6 -> encodeDecodeRot13(inputText)
                    7 -> encodeQuotedPrintable(inputText)
                    8 -> encodePunycode(inputText)
                    else -> inputText
                }
            } else {
                when (selectedMethod) {
                    0 -> decodeBase64(inputText)
                    1 -> decodeUrl(inputText)
                    2 -> decodeHex(inputText)
                    3 -> decodeAscii(inputText)
                    4 -> decodeUtf8(inputText.toByteArray())
                    5 -> decodeBinary(inputText)
                    6 -> encodeDecodeRot13(inputText)
                    7 -> decodeQuotedPrintable(inputText)
                    8 -> decodePunycode(inputText)
                    else -> inputText
                }
            }
        } catch (e: Exception) {
            CustomToast.show(context, context.getString(R.string.toast_error))
            inputText
        }
    }
}
