package com.kurnia.ticket_wosh_app.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Alamat IP server PHP Native.
    // Ganti IP_LAPTOP_KAMU dengan IP laptop/komputer Anda (misal: 192.168.100.12 atau 10.0.2.2 untuk emulator Android)
    var IP_LAPTOP_KAMU = "192.168.100.12"
    
    val BASE_URL: String
        get() = "http://$IP_LAPTOP_KAMU/wosh_api/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    /**
     * Helper untuk mengubah alamat IP secara dinamis dari pengaturan atau saat runtime jika diperlukan
     */
    fun updateIpAddress(newIp: String) {
        IP_LAPTOP_KAMU = newIp
    }
}
