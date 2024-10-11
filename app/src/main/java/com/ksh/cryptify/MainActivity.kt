package com.ksh.cryptify

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.ksh.cryptify.MainPage.MainPageActivity
import com.ksh.cryptify.Utility.Language
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        var savedLanguage = sharedPreferences.getString("language", null)

        // 저장된 언어가 없는 경우 기기의 지역 정보 기반으로 기본 언어 설정
        if (savedLanguage == null) {
            val defaultCountry = Locale.getDefault().country // 기기의 국가 코드
            savedLanguage = when (defaultCountry) {
                "KR" -> "ko" // 대한민국 (한국어)
                "US" -> "en" // 미국 (영어)
                "JP" -> "ja" // 일본 (일본어)
                "CN" -> "zh" // 중국 (중국어)
                "IN" -> "hi" // 인도 (힌디어)
                "PK" -> "ur" // 파키스탄 (우르두어)
                "ES" -> "es" // 스페인 (스페인어)
                "SA" -> "ar" // 사우디아라비아 (아랍어)
                "PT" -> "pt" // 포르투갈 (포르투갈어)
                "BD" -> "bn" // 방글라데시 (벵골어)
                "RU" -> "ru" // 러시아 (러시아어)
                "DE" -> "de" // 독일 (독일어)
                "FR" -> "fr" // 프랑스 (프랑스어)
                "TR" -> "tr" // 터키 (터키어)
                "IT" -> "it" // 이탈리아 (이탈리아어)
                else -> "en" // 기본값: 영어
            }
            sharedPreferences.edit().putString("language", savedLanguage).apply() // 언어 설정 저장
        }

        val languageManager = Language(this)
        languageManager.setLanguage(savedLanguage)

        val savedTheme = sharedPreferences.getInt("theme", 0) // 기본값은 0 (시스템 테마)
        // 저장된 테마에 맞게 모드 설정
        val themeMode = when(savedTheme) {
            0 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            1 -> AppCompatDelegate.MODE_NIGHT_NO
            2 -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        AppCompatDelegate.setDefaultNightMode(themeMode)

        val intent = Intent(this, MainPageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
