package com.kurnia.ticket_wosh_app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kurnia.ticket_wosh_app.databinding.ItemScheduleBinding
import com.kurnia.ticket_wosh_app.model.Schedule
import java.text.NumberFormat
import java.util.Locale

class ScheduleAdapter(
    private var schedules: List<Schedule>,
    private val onSelectClick: (Schedule) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemScheduleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule = schedules[position]
        with(holder.binding) {
            tvTrainName.text = schedule.trainName
            tvDepStation.text = schedule.departureStation
            tvArrStation.text = schedule.arrivalStation
            tvDepartureTime.text = schedule.departureTime
            tvArrivalTime.text = schedule.arrivalTime
            
            // Format Rupiah untuk harga tiket
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            formatter.maximumFractionDigits = 0
            tvPrice.text = formatter.format(schedule.price)

            btnSelect.setOnClickListener {
                onSelectClick(schedule)
            }
        }
    }

    override fun getItemCount() = schedules.size

    fun updateData(newSchedules: List<Schedule>) {
        this.schedules = newSchedules
        notifyDataSetChanged()
    }
}
