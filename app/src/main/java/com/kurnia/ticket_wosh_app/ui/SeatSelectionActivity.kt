package com.kurnia.ticket_wosh_app.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.kurnia.ticket_wosh_app.R
import com.kurnia.ticket_wosh_app.databinding.ActivitySeatSelectionBinding
import com.kurnia.ticket_wosh_app.model.Schedule

class SeatSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeatSelectionBinding
    private var selectedSeatButton: Button? = null
    private var selectedSeatName: String = ""
    private var selectedSeatId: Int = -1
    private lateinit var schedule: Schedule

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data jadwal dari intent
        schedule = intent.getSerializableExtra("SELECTED_SCHEDULE") as Schedule

        setupSeatButtons()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnProceed.setOnClickListener {
            validateAndProceed()
        }
    }

    private fun setupSeatButtons() {
        // Daftar semua button kursi di XML
        val seats = listOf(
            binding.seatA1 to 1, binding.seatB1 to 2, binding.seatC1 to 3, binding.seatD1 to 4,
            binding.seatA2 to 5, binding.seatB2 to 6, binding.seatC2 to 7, binding.seatD2 to 8,
            binding.seatA3 to 9, binding.seatB3 to 10, binding.seatC3 to 11, binding.seatD3 to 12,
            binding.seatA4 to 13, binding.seatB4 to 14, binding.seatC4 to 15, binding.seatD4 to 16
        )

        // Indeks kursi yang statusnya 'terisi' (occupied)
        val occupiedSeatIds = setOf(3, 5, 8, 14) // C1, A2, D2, B4 sesuai warna di XML

        for ((button, seatId) in seats) {
            val isOccupied = occupiedSeatIds.contains(seatId)
            
            if (isOccupied) {
                // Konfigurasi kursi terisi (IMK: visualisasi abu-abu & tidak bisa diklik)
                button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.seatOccupied))
                button.isEnabled = false
            } else {
                // Konfigurasi kursi tersedia (Hijau)
                button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.seatAvailable))
                button.setOnClickListener {
                    selectSeat(button, seatId)
                }
            }
        }
    }

    private fun selectSeat(button: Button, seatId: Int) {
        // Reset warna kursi yang sebelumnya dipilih kembali ke hijau (tersedia)
        selectedSeatButton?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.seatAvailable))

        // Set kursi baru yang dipilih menjadi merah (Whoosh selected)
        button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.seatSelected))
        
        selectedSeatButton = button
        selectedSeatName = button.text.toString()
        selectedSeatId = seatId

        // Tampilkan feedback nama kursi terpilih ke user (IMK Direct Feedback)
        binding.tvSelectedSeatLabel.text = "Kursi Terpilih: $selectedSeatName"
    }

    private fun validateAndProceed() {
        val passengerName = binding.etPassengerName.text.toString().trim()
        val passengerId = binding.etPassengerId.text.toString().trim()

        // IMK: Pencegahan Kesalahan (Error Prevention)
        if (selectedSeatId == -1) {
            Toast.makeText(this, "Silakan pilih salah satu kursi terlebih dahulu!", Toast.LENGTH_SHORT).show()
            return
        }

        if (passengerName.isEmpty()) {
            binding.tilPassengerName.error = "Nama penumpang tidak boleh kosong"
            return
        } else {
            binding.tilPassengerName.error = null
        }

        if (passengerId.isEmpty()) {
            binding.tilPassengerId.error = "Nomor KTP / Paspor tidak boleh kosong"
            return
        } else {
            binding.tilPassengerId.error = null
        }

        // Lanjutkan ke halaman Checkout/Ringkasan Booking
        val intentCheckout = Intent(this, CheckoutActivity::class.java).apply {
            putExtra("SELECTED_SCHEDULE", schedule)
            putExtra("SEAT_NAME", selectedSeatName)
            putExtra("SEAT_ID", selectedSeatId)
            putExtra("PASSENGER_NAME", passengerName)
            putExtra("PASSENGER_ID", passengerId)
        }
        startActivity(intentCheckout)
    }
}
