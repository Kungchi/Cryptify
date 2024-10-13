package com.ksh.cryptify.Page.FeedbackPage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ksh.cryptify.Utility.CustomToast
import com.ksh.cryptify.Utility.RetrofitClient
import com.ksh.cryptify.databinding.ActivityFeedbackBinding

class FeedbackPageActivity : AppCompatActivity() {
    private var _binding: ActivityFeedbackBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSubmit.setOnClickListener {
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmail.text.toString()
            val feedback = binding.editTextFeedback.text.toString()
            if(name.isNotEmpty() && email.isNotEmpty() && feedback.isNotEmpty()) {
                RetrofitClient().sendEmail(name, email, feedback)
            } else {
                CustomToast.show(this,"테스트 내용을 입력해주세요")
            }
        }
    }
}