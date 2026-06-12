package com.kurnia.ticket_wosh_app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kurnia.ticket_wosh_app.databinding.ActivitySearchResultBinding // Berubah sesuai nama XML terbaru Anda
import com.kurnia.ticket_wosh_app.model.Schedule

class SearchResultActivity : AppCompatActivity() {

    // Menggunakan nama class binding dari activity_search_result.xml
    private lateinit var binding: ActivitySearchResultBinding
    private lateinit var adapter: ScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data parameter dari intent halaman pencarian sebelumnya
        val depName = intent.getStringExtra("DEP_NAME") ?: "Asal"
        val arrName = intent.getStringExtra("ARR_NAME") ?: "Tujuan"
        val date = intent.getStringExtra("DATE") ?: ""

        // Membaca array hasil respons list jadwal dari API get_schedules.php
        @Suppress("UNCHECKED_CAST")
        val schedules = intent.getSerializableExtra("SCHEDULES_LIST") as? ArrayList<Schedule> ?: arrayListOf()

        // Pasang data text ke komponen Toolbar Anda
        binding.tvRouteTitle.text = "$depName ➔ $arrName"
        binding.tvTravelDate.text = date

        binding.btnBack.setOnClickListener {
            finish()
        }

        // Siapkan layout manager untuk menampilkan daftar kartu secara vertikal
        binding.rvSchedules.layoutManager = LinearLayoutManager(this)

        // Evaluasi data untuk mengontrol tampilan layout (Aspek Umpan Balik IMK)
        if (schedules.isEmpty()) {
            // Tampilkan komponen empty state bawaan XML pilihan Anda
            binding.rvSchedules.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.layoutError.visibility = View.GONE
        } else {
            binding.rvSchedules.visibility = View.VISIBLE
            binding.layoutEmptyState.visibility = View.GONE
            binding.layoutError.visibility = View.GONE

            // Inisialisasi adapter kartu jadwal
            adapter = ScheduleAdapter(schedules) { selectedSchedule ->
                val intentSeat = Intent(this, SeatSelectionActivity::class.java).apply {
                    putExtra("SELECTED_SCHEDULE", selectedSchedule)
                }
                startActivity(intentSeat)
            }
            binding.rvSchedules.adapter = adapter
        }

        // Tombol aksi Coba Lagi pada state Error layout Anda
        binding.btnRetry.setOnClickListener {
            finish() // Kembali untuk memicu pengisian ulang data form pencarian
        }
    }
}