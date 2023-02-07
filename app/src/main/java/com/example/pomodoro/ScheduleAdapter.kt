package com.example.pomodoro

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView


class ScheduleAdapter(private val DAO: DAO)
    : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {
     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         val view = LayoutInflater.from(parent.context).inflate(R.layout.schedule, parent, false)
         return ViewHolder(view)
     }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = DAO.getSchedules()[position]
        val details = "${item.workTime} / ${item.breakTime} X ${item.intervals}"
        holder.textViewTitle.text = item.title
        holder.textViewDetails.text = details
    }

    override fun getItemCount(): Int = DAO.getSchedules().size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.list_schedule_title)
        val textViewDetails: TextView = itemView.findViewById(R.id.list_schedule_details)
        val iconEdit: ImageView = itemView.findViewById(R.id.icon_edit)

        init {

            itemView.setOnClickListener {
                val schedule = DAO.getSchedules()[bindingAdapterPosition]
                val bundle = Bundle()
                bundle.putInt("id", schedule.id)
                bundle.putString("title", schedule.title)
                bundle.putInt("workTime", schedule.workTime)
                bundle.putInt("breakTime", schedule.breakTime)
                bundle.putInt("intervals", schedule.intervals)
                Log.e("Activate", "${schedule.title}")
                Navigation.findNavController(itemView).navigate(R.id.action_schedule_list_fragment_to_schedule_active_fragment, bundle)
            }

            iconEdit.setOnClickListener {
                val schedule = DAO.getSchedules()[bindingAdapterPosition]
                val bundle = Bundle()
                bundle.putInt("id", schedule.id)
                bundle.putString("title", schedule.title)
                bundle.putInt("workTime", schedule.workTime)
                bundle.putInt("breakTime", schedule.breakTime)
                bundle.putInt("intervals", schedule.intervals)
                Log.e("adapterposition", "AP: $adapterPosition, AAP: $absoluteAdapterPosition, BAP: $bindingAdapterPosition")


                Navigation.findNavController(itemView).navigate(R.id.action_schedule_list_fragment_to_schedule_details_fragment, bundle)
            }

        }
    }



}