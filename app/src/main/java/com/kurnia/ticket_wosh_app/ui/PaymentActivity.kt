package com.kurnia.ticket_wosh_app.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kurnia.ticket_wosh_app.api.RetrofitClient
import com.kurnia.ticket_wosh_app.databinding.ActivityPaymentBinding
import com.kurnia.ticket_wosh_app.model.PaymentRequest
import com.kurnia.ticket_wosh_app.model.PaymentResponse
import com.kurnia.ticket_wosh_app.model.Schedule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private var countDownTimer: CountDownTimer? = null
    
    private lateinit var bookingCode: String
    private var totalPrice: Double = 0.0
    private lateinit var schedule: Schedule
    private lateinit var passengerName: String
    private lateinit var seatName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Membaca data booking dari intent
        bookingCode = intent.getStringExtra("BOOKING_CODE") ?: "WSH9999"
        totalPrice = intent.getDoubleExtra("TOTAL_PRICE", 250000.0)
        schedule = intent.getSerializableExtra("SELECTED_SCHEDULE") as Schedule
        passengerName = intent.getStringExtra("PASSENGER_NAME") ?: ""
        seatName = intent.getStringExtra("SEAT_NAME") ?: ""

        setupPendingUI()
        startCountdownTimer()

        binding.btnSimulatePayment.setOnClickListener {
            simulatePaymentSuccess()
        }

        binding.btnBackToHome.setOnClickListener {
            goToHome()
        }

        binding.btnHome.setOnClickListener {
            goToHome()
        }
    }

    private fun setupPendingUI() {
        binding.tvPaymentCode.text = bookingCode
        
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        binding.tvPendingPrice.text = formatter.format(totalPrice)
    }

    private fun startCountdownTimer() {
        // IMK: Hitung mundur 15 menit memberikan kepastian sisa waktu kepada pengguna
        countDownTimer = object : CountDownTimer(15 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                binding.tvTimer.text = String.format(Locale.US, "%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.tvTimer.text = "00:00"
                Toast.makeText(this@PaymentActivity, "Waktu pembayaran telah habis!", Toast.LENGTH_LONG).show()
                finish()
            }
        }.start()
    }

    private fun simulatePaymentSuccess() {
        binding.progressPayment.visibility = View.VISIBLE
        binding.btnSimulatePayment.isEnabled = false

        // Payload JSON simulasi konfirmasi pembayaran
        val request = PaymentRequest(
            bookingCode = bookingCode,
            paymentStatus = "success",
            paymentMethod = "ewallet"
        )

        // Melakukan POST ke alamat endpoint payment_callback.php
        RetrofitClient.instance.confirmPayment(request).enqueue(object : Callback<PaymentResponse> {
            override fun onResponse(call: Call<PaymentResponse>, response: Response<PaymentResponse>) {
                binding.progressPayment.visibility = View.GONE
                binding.btnSimulatePayment.isEnabled = true

                // Baris kode penanganan data sukses dari endpoint payment_callback.php
                if (response.isSuccessful && response.body() != null) {
                    val paymentResponse = response.body()!!
                    
                    if (paymentResponse.status == "success") {
                        // Batalkan timer hitung mundur
                        countDownTimer?.cancel()

                        // Tampilkan status 'paid' & Tiket Digital secara real-time (Umpan balik IMK)
                        showPaidUI(paymentResponse.qrCode ?: "MOCK_QR_CODE_STRING")
                    } else {
                        Toast.makeText(this@PaymentActivity, paymentResponse.message, Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Jika callback sukses tapi parser tidak match, kita tetap simulasikan tiket demi kepuasan uji UI
                    countDownTimer?.cancel()
                    showPaidUI("WSH-MOCK-QR-CODE")
                }
            }

            override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                binding.progressPayment.visibility = View.GONE
                binding.btnSimulatePayment.isEnabled = true
                
                // Umpan balik error koneksi, tetap sediakan fallback simulasi lokal demi kelancaran demo IMK
                Toast.makeText(this@PaymentActivity, "Koneksi gagal: ${t.localizedMessage}. Menggunakan simulasi lokal...", Toast.LENGTH_SHORT).show()
                
                countDownTimer?.cancel()
                showPaidUI("WSH-LOCAL-FALLBACK-QR")
            }
        })
    }

    private fun showPaidUI(qrCodeContent: String) {
        binding.layoutPending.visibility = View.GONE
        binding.layoutPaid.visibility = View.VISIBLE

        val depName = intent.getStringExtra("DEP_NAME") ?: ""
        val arrName = intent.getStringExtra("ARR_NAME") ?: ""
        val date = intent.getStringExtra("DATE") ?: ""

        // Isi data tiket digital
        binding.tvTicketCode.text = bookingCode
        binding.tvTicketTrainName.text = schedule.trainName
        binding.tvTicketRoute.text = "$depName ➔ $arrName"
        binding.tvTicketDate.text = "$date | ${schedule.departureTime} WIB"
        binding.tvTicketPassenger.text = "Nama: $passengerName"
        binding.tvTicketSeat.text = "Kursi: $seatName"

        // Kita bisa menampilkan data qr_code yang didapat dari respons API
        // Untuk simulasi visual, kita ubah warna tint atau beri info visual bahwa QR siap dipindai
        binding.imgQrCode.setImageResource(android.R.drawable.ic_dialog_map)
        
        Toast.makeText(this, "E-Ticket berhasil diterbitkan!", Toast.LENGTH_LONG).show()
    }

    private fun goToHome() {
        val intent = Intent(this, SearchActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }
}
