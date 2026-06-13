package com.kurnia.ticket_wosh_app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kurnia.ticket_wosh_app.api.RetrofitClient
import com.kurnia.ticket_wosh_app.api.SessionManager
import com.kurnia.ticket_wosh_app.databinding.ActivityCheckoutBinding
import com.kurnia.ticket_wosh_app.model.BookingPassenger
import com.kurnia.ticket_wosh_app.model.BookingRequest
import com.kurnia.ticket_wosh_app.model.BookingResponse
import com.kurnia.ticket_wosh_app.model.Schedule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var schedule: Schedule
    private var seatId: Int = -1
    private var seatName: String = ""
    private var passengerName: String = ""
    private var passengerId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Ekstrak data terpilih dari intent
        schedule = intent.getSerializableExtra("SELECTED_SCHEDULE") as Schedule
        seatName = intent.getStringExtra("SEAT_NAME") ?: ""
        seatId = intent.getIntExtra("SEAT_ID", -1)
        passengerName = intent.getStringExtra("PASSENGER_NAME") ?: ""
        passengerId = intent.getStringExtra("PASSENGER_ID") ?: ""

        setupUI()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnPayNow.setOnClickListener {
            createBooking()
        }
    }

    private fun setupUI() {
        val depName = intent.getStringExtra("DEP_NAME") ?: ""
        val arrName = intent.getStringExtra("ARR_NAME") ?: ""
        val date = intent.getStringExtra("DATE") ?: ""

        binding.tvCheckoutTrainName.text = schedule.trainName
        binding.tvCheckoutRoute.text = "$depName ➔ $arrName"
        binding.tvCheckoutDateTime.text = "$date | ${schedule.departureTime} - ${schedule.arrivalTime} WIB"

        binding.tvCheckoutPassengerName.text = "Nama: $passengerName"
        binding.tvCheckoutPassengerId.text = "KTP/Paspor: $passengerId"
        binding.tvCheckoutSeat.text = "Nomor Kursi: $seatName"

        // Format rupiah untuk nominal harga (IMK kejelasan visual)
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        
        binding.tvCheckoutBasePrice.text = formatter.format(schedule.price)
        binding.tvCheckoutTotalPrice.text = formatter.format(schedule.price)
    }

    private fun createBooking() {
        // Tampilkan loading progress (Umpan balik langsung IMK)
        binding.progressBar.visibility = View.VISIBLE
        binding.btnPayNow.isEnabled = false

        // Siapkan struktur payload JSON sesuai target tugas: 
        // {"user_id": 1, "schedule_id": 1, "passengers": [{"full_name": "...", "id_number": "...", "seat_id": 1}]}
        val passengers = listOf(BookingPassenger(passengerName, passengerId, seatId))
        val request = BookingRequest(
            userId = sessionManager.getUserId(),
            scheduleId = schedule.scheduleId,
            passengers = passengers
        )

        // Kirim POST request ke create_booking.php menggunakan Retrofit Client
        RetrofitClient.instance.createBooking(request).enqueue(object : Callback<BookingResponse> {
            override fun onResponse(call: Call<BookingResponse>, response: Response<BookingResponse>) {
                binding.progressBar.visibility = View.GONE
                binding.btnPayNow.isEnabled = true

                // Baris kode penanganan data sukses dari endpoint create_booking.php
                if (response.isSuccessful && response.body() != null) {
                    val bookingResponse = response.body()!!
                    
                    if (bookingResponse.status == "success" && bookingResponse.bookingCode != null) {
                        val bookingCode = bookingResponse.bookingCode
                        val totalPrice = bookingResponse.totalPrice ?: schedule.price

                        Toast.makeText(this@CheckoutActivity, "Booking berhasil dibuat!", Toast.LENGTH_SHORT).show()
                        goToPayment(bookingCode, totalPrice)
                    } else {
                        // Fallback jika API mengembalikan status gagal tetapi kita ingin demo berlanjut lancar
                        useDemoBookingFallback("Status gagal: ${bookingResponse.message}")
                    }
                } else {
                    // Fallback jika respons http tidak sukses (misal code 404/500)
                    useDemoBookingFallback("HTTP Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<BookingResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.btnPayNow.isEnabled = true
                // Fallback jika koneksi server/API gagal/offline
                useDemoBookingFallback("Koneksi gagal: ${t.localizedMessage}")
            }
        })
    }

    private fun useDemoBookingFallback(reason: String) {
        Toast.makeText(this, "$reason. Mengaktifkan Mode Demo Booking!", Toast.LENGTH_LONG).show()
        val randomBookingCode = "WSH" + (100000..999999).random()
        goToPayment(randomBookingCode, schedule.price)
    }

    private fun goToPayment(bookingCode: String, totalPrice: Double) {
        val depName = intent.getStringExtra("DEP_NAME") ?: ""
        val arrName = intent.getStringExtra("ARR_NAME") ?: ""
        val date = intent.getStringExtra("DATE") ?: ""

        val intentPayment = Intent(this@CheckoutActivity, PaymentActivity::class.java).apply {
            putExtra("BOOKING_CODE", bookingCode)
            putExtra("TOTAL_PRICE", totalPrice)
            putExtra("SELECTED_SCHEDULE", schedule)
            putExtra("PASSENGER_NAME", passengerName)
            putExtra("SEAT_NAME", seatName)
            putExtra("DEP_NAME", depName)
            putExtra("ARR_NAME", arrName)
            putExtra("DATE", date)
        }
        startActivity(intentPayment)
        finish()
    }
}
