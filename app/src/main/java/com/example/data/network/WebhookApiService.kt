package com.example.data.network

import com.example.data.model.SmsWebhookPayload
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

interface WebhookApiService {

    @POST
    suspend fun sendWebhook(
        @Url url: String,
        @Header("Authorization") token: String?,
        @Header("Content-Type") contentType: String = "application/json",
        @Body payload: SmsWebhookPayload
    ): Response<ResponseBody>

    companion object {
        fun create(): WebhookApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://localhost/") // Base URL placeholder; actual request passes full @Url
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

            return retrofit.create(WebhookApiService::class.java)
        }
    }
}
