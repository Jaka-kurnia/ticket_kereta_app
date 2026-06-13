package com.kurnia.ticket_wosh_app.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Alamat IP server PHP Native.
    // Ganti IP_LAPTOP_KAMU dengan IP laptop/komputer Anda (misal: 192.168.100.2 atau 10.0.2.2 untuk emulator Android)
    var IP_LAPTOP_KAMU = "10.10.201.96"
    
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

    private var apiServiceInstance: ApiService? = null

    val instance: ApiService
        get() {
            if (apiServiceInstance == null) {
                apiServiceInstance = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService::class.java)
            }
            return apiServiceInstance!!
        }

    /**
     * Helper untuk mengubah alamat IP secara dinamis dari pengaturan atau saat runtime jika diperlukan
     */
    fun updateIpAddress(newIp: String) {
        if (IP_LAPTOP_KAMU != newIp) {
            IP_LAPTOP_KAMU = newIp
            // Reset instance agar Retrofit di-build ulang dengan base URL yang baru saat dipanggil
            apiServiceInstance = null
        }
    }
}
