package com.kurnia.ticket_wosh_app.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kurnia.ticket_wosh_app.api.RetrofitClient
import com.kurnia.ticket_wosh_app.api.SessionManager
import com.kurnia.ticket_wosh_app.databinding.ActivitySearchBinding
import com.kurnia.ticket_wosh_app.model.Schedule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.Locale

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var sessionManager: SessionManager

    // Map stasiun ke ID numerik sesuai target API get_schedules.php
    private val stationMap = mapOf(
        "Halim" to "1",
        "Karawang" to "2",
        "Padalarang" to "3",
        "Tegalluar" to "4"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Menampilkan nama session aktif
        val name = sessionManager.getFullName()
        binding.tvWelcome.text = "Halo, $name"

        setupDropdowns()
        setupDatePicker()

        binding.btnSearch.setOnClickListener {
            performSearch()
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupDropdowns() {
        val stations = stationMap.keys.toList()
        val adapterDep = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, stations)
        val adapterArr = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, stations)

        binding.actDepartureStation.setAdapter(adapterDep)
        binding.actArrivalStation.setAdapter(adapterArr)
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(Locale.US, "%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                binding.etDate.setText(formattedDate)
            }, year, month, day)

            // Batasi agar tidak bisa memilih tanggal sebelum hari ini
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }
    }

    private fun performSearch() {
        val depName = binding.actDepartureStation.text.toString()
        val arrName = binding.actArrivalStation.text.toString()
        val date = binding.etDate.text.toString()

        // IMK: Pencegahan Kesalahan (Error Prevention)
        if (depName.isEmpty()) {
            binding.actDepartureStation.error = "Pilih stasiun asal"
            return
        }

        if (arrName.isEmpty()) {
            binding.actArrivalStation.error = "Pilih stasiun tujuan"
            return
        }

        if (depName == arrName) {
            Toast.makeText(this, "Stasiun asal dan tujuan tidak boleh sama!", Toast.LENGTH_LONG).show()
            return
        }

        if (date.isEmpty()) {
            binding.etDate.error = "Pilih tanggal keberangkatan"
            return
        }

        val depId = stationMap[depName] ?: "1"
        val arrId = stationMap[arrName] ?: "3"

        Toast.makeText(this, "Mencari jadwal dari $depName ke $arrName...", Toast.LENGTH_SHORT).show()

        // Panggil API get_schedules.php
        RetrofitClient.instance.getSchedules(depId, arrId, date).enqueue(object : Callback<List<Schedule>> {
            override fun onResponse(call: Call<List<Schedule>>, response: Response<List<Schedule>>) {
                if (response.isSuccessful && response.body() != null && response.body()!!.isNotEmpty()) {
                    val scheduleList = response.body()!!
                    goToResults(depName, arrName, date, ArrayList(scheduleList))
                } else {
                    // Fallback jika respons kosong / data di database server masih kosong
                    useDemoFallback(depName, arrName, date, "Jadwal kosong di database.")
                }
            }

            override fun onFailure(call: Call<List<Schedule>>, t: Throwable) {
                // Fallback jika server backend offline / IP salah konfigurasi
                useDemoFallback(depName, arrName, date, "Koneksi gagal: ${t.localizedMessage}")
            }
        })
    }

    private fun useDemoFallback(depName: String, arrName: String, date: String, reason: String) {
        Toast.makeText(this, "$reason Mengaktifkan Mode Demo Whoosh!", Toast.LENGTH_LONG).show()
        
        // Buat data jadwal tiruan (realistic mock data) agar user bisa melanjutkan alur tanpa error
        val mockSchedules = arrayListOf(
            Schedule(1, "Whoosh Premium G1102", depName, arrName, "08:00", "08:45", 250000.0, date),
            Schedule(2, "Whoosh Business G1203", depName, arrName, "11:30", "12:15", 300000.0, date),
            Schedule(3, "Whoosh Fast G1508", depName, arrName, "15:45", "16:30", 250000.0, date)
        )
        goToResults(depName, arrName, date, mockSchedules)
    }

    private fun goToResults(depName: String, arrName: String, date: String, list: ArrayList<Schedule>) {
        val intent = Intent(this@SearchActivity, SearchResultActivity::class.java).apply {
            putExtra("DEP_NAME", depName)
            putExtra("ARR_NAME", arrName)
            putExtra("DATE", date)
            putExtra("SCHEDULES_LIST", list)
        }
        startActivity(intent)
    }
}
