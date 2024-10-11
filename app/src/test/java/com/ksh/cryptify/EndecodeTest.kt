package com.ksh.cryptify

import com.ksh.cryptify.Utility.Endecode
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EndecodeTest {

    private lateinit var endecode: Endecode

    @Before
    fun setUp() {
        endecode = Endecode()
    }

    // Base64 인코딩/디코딩 테스트
    @Test
    fun testBase64EncodingAndDecoding() {
        val testCases = listOf(
            "Hello, World!",
            "안녕하세요",
            "こんにちは",
            "你好",
            "مرحبا",
            "Привет"
        )
        for (original in testCases) {
            val encoded = endecode.encodeBase64(original)
            val decoded = endecode.decodeBase64(encoded)
            assertEquals("Base64 failed for: $original", original, decoded)
        }
    }

    // URL 인코딩/디코딩 테스트
    @Test
    fun testUrlEncodingAndDecoding() {
        val testCases = listOf(
            "https://example.com?query=Hello",
            "https://example.com?query=안녕하세요",
            "https://example.com?query=こんにちは",
            "https://example.com?query=你好",
            "https://example.com?query=مرحبا",
            "https://example.com?query=Привет"
        )
        for (original in testCases) {
            val encoded = endecode.encodeUrl(original)
            val decoded = endecode.decodeUrl(encoded)
            assertEquals("URL encoding failed for: $original", original, decoded)
        }
    }

    // Hexadecimal 인코딩/디코딩 테스트
    @Test
    fun testHexEncodingAndDecoding() {
        val testCases = listOf(
            "Hexadecimal Test",
            "안녕하세요",
            "こんにちは",
            "你好",
            "مرحبا",
            "Привет"
        )
        for (original in testCases) {
            val encoded = endecode.encodeHex(original)
            val decoded = endecode.decodeHex(encoded)
            assertEquals("Hex encoding failed for: $original", original, decoded)
        }
    }

    // ASCII 인코딩/디코딩 테스트
    @Test
    fun testAsciiEncodingAndDecoding() {
        val original = "ASCII Test"
        val encoded = endecode.encodeAscii(original)
        val decoded = endecode.decodeAscii(encoded)
        assertEquals("ASCII encoding failed for: $original", original, decoded)
    }

    // UTF-8 인코딩/디코딩 테스트
    @Test
    fun testUtf8EncodingAndDecoding() {
        val testCases = listOf(
            "UTF-8 Test",
            "안녕하세요",
            "こんにちは",
            "你好",
            "مرحبا",
            "Привет"
        )
        for (original in testCases) {
            val encoded = endecode.encodeUtf8(original)
            val decoded = endecode.decodeUtf8(encoded)
            assertEquals("UTF-8 encoding failed for: $original", original, decoded)
        }
    }

    // Binary 인코딩/디코딩 테스트
    @Test
    fun testBinaryEncodingAndDecoding() {
        val testCases = listOf(
            "Binary Test",
            "안녕하세요",
            "こんにちは",
            "你好",
            "مرحبا",
            "Привет"
        )
        for (original in testCases) {
            val encoded = endecode.encodeBinary(original)
            val decoded = endecode.decodeBinary(encoded)
            assertEquals("Binary encoding failed for: $original", original, decoded)
        }
    }

    // ROT13 인코딩/디코딩 테스트
    @Test
    fun testRot13EncodingAndDecoding() {
        val original = "Hello, World!"
        val encoded = endecode.encodeDecodeRot13(original)
        val decoded = endecode.encodeDecodeRot13(encoded)  // ROT13은 동일한 함수로 인코딩/디코딩
        assertEquals("ROT13 encoding failed for: $original", original, decoded)
    }

    // Quoted-Printable 인코딩/디코딩 테스트
    @Test
    fun testQuotedPrintableEncodingAndDecoding() {
        val testCases = listOf(
            "Quoted=Printable Test",
            "안녕하세요",
            "こんにちは",
            "你好",
            "مرحبا",
            "Привет"
        )
        for (original in testCases) {
            val encoded = endecode.encodeQuotedPrintable(original)
            val decoded = endecode.decodeQuotedPrintable(encoded)
            assertEquals("Quoted-Printable encoding failed for: $original", original, decoded)
        }
    }

    // Punycode 인코딩/디코딩 테스트
    @Test
    fun testPunycodeEncodingAndDecoding() {
        val testCases = listOf(
            "안녕하세요",
            "こんにちは",
            "你好",
            "مرحبا",
            "Привет"
        )
        for (original in testCases) {
            val encoded = endecode.encodePunycode(original)
            val decoded = endecode.decodePunycode(encoded)

            // 대소문자 구분 없이 비교
            assertEquals("Punycode encoding failed for: $original", original.lowercase(), decoded.lowercase())
        }
    }
}
