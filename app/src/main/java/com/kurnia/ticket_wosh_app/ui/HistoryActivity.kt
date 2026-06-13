package com.kurnia.ticket_wosh_app.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kurnia.ticket_wosh_app.api.RetrofitClient
import com.kurnia.ticket_wosh_app.api.SessionManager
import com.kurnia.ticket_wosh_app.databinding.ActivityHistoryBinding
import com.kurnia.ticket_wosh_app.model.BookingHistory
import com.kurnia.ticket_wosh_app.model.BookingHistoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var sessionManager: SessionManager
    private val historyList = mutableListOf<BookingHistory>()
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.btnBack.setOnClickListener {
            finish()
        }

        setupRecyclerView()
        fetchHistory()
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(historyList)
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter
    }

    private fun fetchHistory() {
        val userId = sessionManager.getUserId()

        binding.progressBar.visibility = View.VISIBLE
        binding.rvHistory.visibility = View.GONE
        binding.tvEmptyState.visibility = View.GONE

        RetrofitClient.instance.getHistory(userId).enqueue(object : Callback<BookingHistoryResponse> {
            override fun onResponse(call: Call<BookingHistoryResponse>, response: Response<BookingHistoryResponse>) {
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!.data
                    if (data != null && data.isNotEmpty()) {
                        historyList.clear()
                        historyList.addAll(data)
                        adapter.notifyDataSetChanged()
                        binding.rvHistory.visibility = View.VISIBLE
                    } else {
                        binding.tvEmptyState.visibility = View.VISIBLE
                    }
                } else {
                    binding.tvEmptyState.text = "Gagal memuat riwayat tiket."
                    binding.tvEmptyState.visibility = View.VISIBLE
                    Toast.makeText(this@HistoryActivity, "Respon server tidak sesuai", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BookingHistoryResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                binding.tvEmptyState.text = "Tidak ada koneksi jaringan."
                binding.tvEmptyState.visibility = View.VISIBLE
                
                Toast.makeText(this@HistoryActivity, "Error koneksi: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
