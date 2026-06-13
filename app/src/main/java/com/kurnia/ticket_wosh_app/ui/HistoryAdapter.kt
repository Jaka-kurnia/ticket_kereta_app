package com.kurnia.ticket_wosh_app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kurnia.ticket_wosh_app.databinding.ItemHistoryBinding
import com.kurnia.ticket_wosh_app.model.BookingHistory
import java.text.NumberFormat
import java.util.Locale

class HistoryAdapter(private val historyList: List<BookingHistory>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]
        
        holder.binding.tvHistoryBookingCode.text = history.bookingCode
        holder.binding.tvHistoryTrainName.text = history.trainName
        holder.binding.tvHistoryRoute.text = "${history.departureStation} ➔ ${history.arrivalStation}"
        holder.binding.tvHistoryDateTime.text = "${history.date} | ${history.departureTime} - ${history.arrivalTime}"
        
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        holder.binding.tvHistoryPrice.text = formatter.format(history.totalPrice)

        // Styling status (pending vs paid/success)
        holder.binding.tvHistoryStatus.text = history.status.uppercase(Locale.getDefault())
        if (history.status.equals("pending", true)) {
            holder.binding.tvHistoryStatus.setBackgroundResource(com.kurnia.ticket_wosh_app.R.drawable.circle_bg_gray)
            holder.binding.tvHistoryStatus.setTextColor(android.graphics.Color.parseColor("#333333"))
        } else {
            holder.binding.tvHistoryStatus.setBackgroundResource(com.kurnia.ticket_wosh_app.R.drawable.circle_bg_green)
            holder.binding.tvHistoryStatus.setTextColor(android.graphics.Color.WHITE)
        }
    }

    override fun getItemCount(): Int {
        return historyList.size
    }
}
