package com.example.pomodoro

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

class ScheduleDetailsFragment: Fragment() {
    private lateinit var DAO: DAO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.schedule_details_fragment, container, false)
        val id: Int? = arguments?.getInt("id")

        context?.let {
            DAO = DAO(it)
        }

        id?.let {
            view.findViewById<EditText>(R.id.schedule_title).setText(arguments?.getString("title"))
            view.findViewById<EditText>(R.id.schedule_work_time).setText(arguments?.getInt("workTime").toString())
            view.findViewById<EditText>(R.id.schedule_break_time).setText(arguments?.getInt("breakTime").toString())
            view.findViewById<EditText>(R.id.schedule_intervals).setText(arguments?.getInt("intervals").toString())
        }

        fun goBack() = Navigation.findNavController(view).navigate(
            ScheduleDetailsFragmentDirections.actionScheduleDetailsFragmentToScheduleListFragment()
        )

        view.findViewById<Button>(R.id.save_button).setOnClickListener {
                val title: String = view.findViewById<EditText>(R.id.schedule_title).text.toString()
                val workTime: String = view.findViewById<EditText>(R.id.schedule_work_time).text.toString()
                val breakTime: String = view.findViewById<EditText>(R.id.schedule_break_time).text.toString()
                val intervals: String = view.findViewById<EditText>(R.id.schedule_intervals).text.toString()
                if (title.isNotBlank() && workTime.isNotBlank() && breakTime.isNotBlank() && intervals.isNotBlank()) {
                    id?.let {
                        DAO.updateSchedule(id, title, workTime.toInt(), breakTime.toInt(), intervals.toInt())
                    } ?: DAO.addSchedule(Schedule(
                        0,
                        title,
                        workTime.toInt(),
                        breakTime.toInt(),
                        intervals.toInt()
                    ))
                }
            goBack()
        }
        view.findViewById<Button>(R.id.delete_button).setOnClickListener {
            id?.let {
                DAO.deleteSchedule(id)
            }
            goBack()
        }
        return view
    }
}