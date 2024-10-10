package com.ksh.cryptify.Utility

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.ksh.cryptify.R

class CustomToast {
    companion object {
        fun show(context: Context, message: String, upDown : Boolean = true) {
            val inflater = LayoutInflater.from(context)
            val layout = inflater.inflate(R.layout.custom_toast, null)

            // 커스텀 레이아웃의 TextView에 메시지 설정
            val textView = layout.findViewById<TextView>(R.id.custom_toast_message)
            textView.text = message

            // 커스텀 Toast 생성
            val toast = Toast(context)
            toast.duration = Toast.LENGTH_SHORT
            toast.view = layout

            // 위치 조정 (예: 화면 상단에 표시)
            if(upDown) {
                toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100)
            } else {
                toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 100)
            }
            toast.show()
        }
    }
}
