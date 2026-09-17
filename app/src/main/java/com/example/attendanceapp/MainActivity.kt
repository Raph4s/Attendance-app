package com.example.attendanceapp

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var timePicker: TimePicker
    private lateinit var spinnerStatus: Spinner
    private lateinit var etKeterangan: EditText
    private lateinit var btnSubmit: MaterialButton
    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendarView = findViewById(R.id.calendarView)
        timePicker = findViewById(R.id.timePicker)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        etKeterangan = findViewById(R.id.etKeterangan)
        btnSubmit = findViewById(R.id.btn_submit)
        tvResult = findViewById(R.id.tvResult)

        setupSpinner()
        setupSubmit()
        setupExitDialog()
    }

    private fun setupSpinner() {
        val statusList = resources.getStringArray(R.array.status_array)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, statusList)
        spinnerStatus.adapter = adapter

        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Index 0 = "Hadir Tepat Waktu" -> no extra note needed.
                // Anything else (Sakit, Terlambat, Izin) -> show the Keterangan field.
                etKeterangan.visibility = if (position == 0) View.GONE else View.VISIBLE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSubmit() {
        btnSubmit.setOnClickListener {
            val selectedDateMillis = calendarView.date
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            val dateText = dateFormat.format(selectedDateMillis)

            val hour: Int
            val minute: Int
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                hour = timePicker.hour
                minute = timePicker.minute
            } else {
                @Suppress("DEPRECATION")
                hour = timePicker.currentHour
                @Suppress("DEPRECATION")
                minute = timePicker.currentMinute
            }

            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val timeText = timeFormat.format(cal.time)

            tvResult.text = "Presensi berhasil $dateText jam $timeText"
            tvResult.visibility = View.VISIBLE
        }
    }

    // "fitur exit custom dialog": intercept the back button and show a custom
    // confirmation dialog instead of exiting immediately.
    private fun setupExitDialog() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })
    }

    private fun showExitDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_exit)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)

        val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnCancel)
        val btnExit = dialog.findViewById<MaterialButton>(R.id.btnExit)

        btnCancel.setOnClickListener { dialog.dismiss() }
        btnExit.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }
}