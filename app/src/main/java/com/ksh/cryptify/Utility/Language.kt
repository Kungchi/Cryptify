package com.ksh.cryptify.Utility

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.view.View
import java.util.Locale

class Language(private val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    // 언어 설정 적용
    fun setLanguage(languageCode: String) {
        sharedPreferences.edit().putString("language", languageCode).apply()
        applyLocale(languageCode)

        // RTL 지원 여부에 따라 레이아웃 방향을 즉시 변경
        val locale = Locale(languageCode)
        val isRtl = TextUtils.getLayoutDirectionFromLocale(locale) == View.LAYOUT_DIRECTION_RTL

        val decorView = (context as Activity).window.decorView
        decorView.layoutDirection = if (isRtl) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR

        // 프래그먼트 뷰를 다시 그려줍니다.
        (context as Activity).window.decorView.invalidate()
    }


    // 로케일 설정 적용
     fun applyLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = context.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
