package com.fahri.ppdb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.time.LocalDate

class AturTanggalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_atur_tanggal)

        val cvBack = findViewById<CardView>(R.id.cvBack)

        // Handle tombol kembali menggunakan CardView
        cvBack.setOnClickListener {
            finish() // Menutup activity sepenuhnya
        }

        val datePickerStart: DatePicker = findViewById(R.id.datePickerStart)
        val datePickerEnd: DatePicker = findViewById(R.id.datePickerEnd)
        val buttonSubmit: Button = findViewById(R.id.buttonSubmit)

        buttonSubmit.setOnClickListener {
            val startYear = datePickerStart.year
            val startMonth = datePickerStart.month + 1 // Month is 0-indexed
            val startDay = datePickerStart.dayOfMonth

            val endYear = datePickerEnd.year
            val endMonth = datePickerEnd.month + 1 // Month is 0-indexed
            val endDay = datePickerEnd.dayOfMonth

            val startDate = LocalDate.of(startYear, startMonth, startDay)
            val endDate = LocalDate.of(endYear, endMonth, endDay)

            // Simpan ke SharedPreferences
            val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("start_date", startDate.toString())
            editor.putString("end_date", endDate.toString())
            editor.apply()

// Log untuk memverifikasi penyimpanan
            Log.d("SharedPreferences", "Start Date saved: ${startDate.toString()}, End Date saved: ${endDate.toString()}")

            // Kirim ke MainActivity (opsional)
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("start_date", startDate.toString())
            intent.putExtra("end_date", endDate.toString())
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}