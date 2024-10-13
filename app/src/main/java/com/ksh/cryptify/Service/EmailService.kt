package com.ksh.cryptify.Service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface EmailService {
    @Headers("content-type: application/json")
    @POST("/api/v1.0/email/send")
    fun sendEmail(@Body data: DTO) : Call<String>
}

data class DTO(
    val service_id : String,
    val template_id : String,
    val user_id : String,
    val template_params : Map<String,String>
)