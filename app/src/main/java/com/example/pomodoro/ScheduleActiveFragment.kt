package com.example.pomodoro

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import kotlin.concurrent.thread
import kotlin.math.roundToInt
import kotlin.properties.Delegates
import kotlin.random.Random

class ScheduleActiveFragment: Fragment() {
    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0
    private var interval = 0
    private var isWorkPhase = false
    private var timeLeft: Double = 0.0

    private lateinit var viewCurrentPhase: TextView
    private lateinit var viewTimer: TextView
    private lateinit var viewNextPhase: TextView
    private lateinit var viewTimeLeft: TextView
    private lateinit var viewButtonStartStop: Button
    private lateinit var viewButtonQuit: Button

    private lateinit var title: String
    private var workTime by Delegates.notNull<Int>()
    private var  breakTime by Delegates.notNull<Int>()
    private var intervals by Delegates.notNull<Int>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.schedule_active_fragment, container, false)

        viewCurrentPhase = view.findViewById(R.id.schedule_active_current_phase)
        viewTimer = view.findViewById(R.id.schedule_active_timer)
        viewNextPhase = view.findViewById(R.id.schedule_active_next_phase)
        viewTimeLeft = view.findViewById(R.id.schedule_active_time_left)
        viewButtonStartStop = view.findViewById(R.id.schedule_active_button_start_stop)
        viewButtonQuit = view.findViewById(R.id.schedule_active_button_quit)

        val id = arguments?.getInt("id")
        title = arguments?.getString("title").toString()
        workTime = arguments?.getInt("workTime").toString().toInt()
        breakTime = arguments?.getInt("breakTime").toString().toInt()
        intervals = arguments?.getInt("intervals").toString().toInt()


        time = workTime.toDouble() * 60
        timeLeft = getInitialTimeLeft()

        viewTimer.text = getTimeStringFromDouble(time)
        viewNextPhase.text = getTimeStringFromDouble(breakTime.toDouble() * 60)
        viewTimeLeft.text = getTimeStringFromDouble(timeLeft * 60)




        viewButtonStartStop.setOnClickListener {
            if (interval == 0) {
                nextPhase()
            } else {
                startStopTimer()
            }
            // if full cycle passed, restart
            if (interval == intervals) {
                interval = 0
                isWorkPhase = false
                time = workTime.toDouble() * 60
                timeLeft = getInitialTimeLeft()
                viewNextPhase.text = getTimeStringFromDouble(breakTime.toDouble() * 60)
            }
        }

        viewButtonQuit.setOnClickListener {
            stopTimer()
            context?.unregisterReceiver(updateTime)
            Navigation.findNavController(view).navigate(
                R.id.action_schedule_active_fragment_to_schedule_list_fragment
            )
        }

        serviceIntent = Intent(context, TimerService::class.java)
        context?.registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))


        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
        context?.unregisterReceiver(updateTime)
    }

    private fun startStopTimer() {
        if (timerStarted)
            stopTimer()
        else
            startTimer()
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        context?.startService(serviceIntent)
        viewButtonStartStop.text = "STOP"
        timerStarted = true
    }

    private fun stopTimer() {
        context?.stopService(serviceIntent)
        viewButtonStartStop.text = "START"
        timerStarted = false
    }

     private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
         override fun onReceive(context: Context?, intent: Intent?) {
             if (intent != null) {
                 time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
                 if (time <= 0) {
                     stopTimer()
                     flashTimer()
                     vibratePhone(200L)
                     nextPhase() // startTimer in nextPhase
                 }
             }
             viewTimer.text = getTimeStringFromDouble(time)
         }
     }

     private fun getTimeStringFromDouble(time: Double): CharSequence? {
        val resultInt = time.roundToInt()
         val minutes = resultInt % 86480 % 3600 / 60
         val seconds = resultInt % 86480 % 3600 % 60

         return makeTimeString(minutes, seconds)
     }

    private fun nextPhase() {
        Log.e("Interval", "$interval")
        Log.e("Intervals", "$intervals")
        if (interval == intervals) {
            return
        }

        if (isWorkPhase) {
            Log.e("Phase", "break")
            isWorkPhase = false
            time = breakTime.toDouble() * 60
            viewCurrentPhase.text = "BREAK"
            viewNextPhase.text = getTimeStringFromDouble(workTime.toDouble() * 60)
            timeLeft -= workTime
        } else {
            Log.e("Phase", "Work")
            interval += 1
            isWorkPhase = true
            time = workTime.toDouble() * 60
            viewCurrentPhase.text = "WORK"
            timeLeft -= breakTime
            if (interval == intervals) {
                viewNextPhase.text = getTimeStringFromDouble(0.0)
            }
        }
        viewTimeLeft.text = getTimeStringFromDouble(timeLeft * 60)
        startTimer()
    }

    private fun flashTimer() {
        thread(true) {
            repeat(3) {
                viewTimer.setTextColor(Color.parseColor("#00FFFFFF"))
                Thread.sleep(300L)
                viewTimer.setTextColor(Color.parseColor("#FFFFFF"))
                Thread.sleep(300L)
            }
        }
    }

    private fun vibratePhone(time: Long) {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(time)
        }
    }

    private fun getInitialTimeLeft(): Double = ((workTime + breakTime) * intervals - breakTime).toDouble()

    private fun makeTimeString(min: Int, sec: Int): String = String.format("%02d:%02d", min, sec)
}