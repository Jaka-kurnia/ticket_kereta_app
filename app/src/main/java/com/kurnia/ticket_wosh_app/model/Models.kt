package com.kurnia.ticket_wosh_app.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// Halaman 1: Registrasi Request & Response
data class RegisterRequest(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("phone") val phone: String
) : Serializable

data class RegisterResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("user_id") val userId: Int?
) : Serializable

// Halaman 1: Login Request & Response
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
) : Serializable

data class LoginResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("phone") val phone: String?
) : Serializable

// Halaman 3: List Jadwal Model
data class Schedule(
    @SerializedName("schedule_id") val scheduleId: Int,
    @SerializedName("train_name") val trainName: String,
    @SerializedName("train_code") val trainCode: String?,
    @SerializedName("departure_time") val departureTime: String,
    @SerializedName("arrival_time") val arrivalTime: String,
    @SerializedName("price") val price: Double
) : Serializable

data class ScheduleResponse(
    @SerializedName("status") val status: String,
    @SerializedName("total_found") val totalFound: Int,
    @SerializedName("data") val data: List<Schedule>
) : Serializable

// Halaman 5: Booking Request & Response
data class BookingPassenger(
    @SerializedName("full_name") val fullName: String,
    @SerializedName("id_number") val idNumber: String,
    @SerializedName("seat_id") val seatId: Int
)

data class BookingRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("schedule_id") val scheduleId: Int,
    @SerializedName("passengers") val passengers: List<BookingPassenger>
)

data class BookingResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("booking_code") val bookingCode: String?,
    @SerializedName("total_price") val totalPrice: Double?
)

// Halaman 6: Payment Request & Response
data class PaymentRequest(
    @SerializedName("booking_code") val bookingCode: String,
    @SerializedName("payment_status") val paymentStatus: String,
    @SerializedName("payment_method") val paymentMethod: String
)

data class PaymentResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("qr_code") val qrCode: String?
)
