package com.kurnia.ticket_wosh_app.api

import com.kurnia.ticket_wosh_app.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    // Halaman 1: Registrasi User
    // Mengirim data JSON pendaftaran ke register.php
    @POST("register.php")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>

    // Halaman 1: Login User
    // Mengirim data JSON email & password ke login.php
    @POST("login.php")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>

    // Halaman 2: Pencarian Jadwal
    // Mengambil daftar jadwal berdasarkan stasiun asal, tujuan, dan tanggal
    @GET("get_schedules.php")
    fun getSchedules(
        @Query("departure_station") departureStation: String,
        @Query("arrival_station") arrivalStation: String,
        @Query("date") date: String
    ): Call<ScheduleResponse>

    // Halaman 5: Create Booking / Checkout
    // Mengirim data booking tiket untuk mendapatkan kode booking dan total bayar
    @POST("create_booking.php")
    fun createBooking(@Body request: BookingRequest): Call<BookingResponse>

    // Halaman 6: Konfirmasi Pembayaran (Callback Simulasi)
    // Mengubah status transaksi menjadi sukses (paid) dan mendapatkan QR Code tiket
    @POST("payment_callback.php")
    fun confirmPayment(@Body request: PaymentRequest): Call<PaymentResponse>

    // Halaman History: Riwayat Pemesanan
    @GET("get_history.php")
    fun getHistory(@Query("user_id") userId: Int): Call<BookingHistoryResponse>
}
