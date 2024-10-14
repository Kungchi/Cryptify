package com.ksh.cryptify.Page.FeedbackPage

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.ksh.cryptify.BuildConfig
import com.ksh.cryptify.R
import com.ksh.cryptify.Service.DTO
import com.ksh.cryptify.Service.EmailService
import com.ksh.cryptify.Utility.CustomToast
import com.ksh.cryptify.databinding.ActivityFeedbackBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
            binding.progressBar.visibility = View.VISIBLE
            if(name.isNotEmpty() && email.isNotEmpty() && feedback.isNotEmpty()) {
                sendEmail(name, email, feedback)
            } else {
                CustomToast.show(this, getString(R.string.content_please))
            }
        }
    }

    fun sendEmail(
        name: String,
        email: String,
        feedback: String,
    ) {

        var gson= GsonBuilder().setLenient().create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.emailjs.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val service = retrofit.create(EmailService::class.java)

        val service_id = BuildConfig.SERVICE_ID
        val template_id = BuildConfig.TEMPLATE_ID
        val user_id = BuildConfig.USER_ID
        val template_params = mapOf(
            "name" to name,
            "email" to email,
            "content" to feedback
        )

        val dto = DTO(service_id, template_id, user_id, template_params)

        service.sendEmail(dto).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful) {
                    Log.d("이메일 확인용", "성공했습니다 : ${response.body()}")
                    CustomToast.show(this@FeedbackPageActivity, getString(R.string.Retrofit_Success))
                    finish()
                } else {
                    Log.d("이메일 확인용", "이메일 전송에 실패했습니다. : ${response.errorBody()?.string()}")
                    CustomToast.show(this@FeedbackPageActivity, getString(R.string.Retrofit_Fail))
                    finish()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("이메일 확인용", "그냥 아싸리 실패했습니다. : ${t.message}")
                CustomToast.show(this@FeedbackPageActivity, getString(R.string.Retrofit_Fail))
                finish()
            }
        })
    }
}