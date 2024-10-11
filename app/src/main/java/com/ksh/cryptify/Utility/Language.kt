package com.ksh.cryptify.Utility

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.text.TextUtils
import android.view.View
import java.util.Locale

class Language(private val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    // 언어 설정 적용
    fun setLanguage(languageCode: String) {
        sharedPreferences.edit().putString("language", languageCode).apply()

        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ) { config.setLocale(locale); }
        else { config.locale = locale; }
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        // RTL 지원 여부에 따라 레이아웃 방향을 즉시 변경
        val isRtl = TextUtils.getLayoutDirectionFromLocale(locale) == View.LAYOUT_DIRECTION_RTL

        val decorView = (context as Activity).window.decorView
        decorView.layoutDirection = if (isRtl) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR

        // 프래그먼트 뷰를 다시 그려줍니다.
        (context as Activity).window.decorView.invalidate()
    }

}
