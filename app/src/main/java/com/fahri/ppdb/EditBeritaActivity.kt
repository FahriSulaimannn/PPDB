package com.fahri.ppdb

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class EditBeritaActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var beritaId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_berita)

        // Ambil data dari intent
        beritaId = intent.getStringExtra("beritaId") ?: return
        val judul = intent.getStringExtra("judul")
        val isi = intent.getStringExtra("isi")
        val lokasi = intent.getStringExtra("lokasi")
        val tanggal = intent.getStringExtra("tanggal")

        // Isi data ke EditText
        findViewById<EditText>(R.id.editJudul).setText(judul)
        findViewById<EditText>(R.id.editIsi).setText(isi)
        findViewById<EditText>(R.id.editLokasi).setText(lokasi)
        val editTanggal = findViewById<EditText>(R.id.editTanggal)
        editTanggal.setText(tanggal)

        // Nonaktifkan input manual untuk tanggal dan tambahkan DatePickerDialog
        editTanggal.isFocusable = false
        editTanggal.isClickable = true
        editTanggal.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format tanggal ke dalam string DD-MM-YYYY
                val formattedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                editTanggal.setText(formattedDate)
            }, year, month, day)

            datePickerDialog.show()
        }

        // Inisialisasi Firebase
        database = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("berita")

        // Tombol Simpan
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val updatedJudul = findViewById<EditText>(R.id.editJudul).text.toString()
            val updatedLokasi = findViewById<EditText>(R.id.editLokasi).text.toString()
            val updatedIsi = findViewById<EditText>(R.id.editIsi).text.toString()
            val updatedTanggal = findViewById<EditText>(R.id.editTanggal).text.toString()

            // Simpan perubahan ke Firebase
            database.child(beritaId).updateChildren(
                mapOf(
                    "judul" to updatedJudul,
                    "isi" to updatedIsi,
                    "lokasi" to updatedLokasi,
                    "tanggal" to updatedTanggal
                )
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Tombol Hapus
        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            // Tampilkan AlertDialog
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("Konfirmasi Penghapusan")
            builder.setMessage("Apakah Anda yakin ingin menghapus berita ini?")

            // Tombol "Ya"
            builder.setPositiveButton("Ya") { dialog, _ ->
                database.child(beritaId).removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Berita berhasil dihapus", Toast.LENGTH_SHORT).show()
                        finish() // Kembali ke aktivitas sebelumnya
                    } else {
                        Toast.makeText(this, "Gagal menghapus berita", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss() // Tutup dialog setelah dipilih
            }

            // Tombol "Tidak"
            builder.setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss() // Tutup dialog tanpa melakukan apa pun
            }

            // Tampilkan dialog
            builder.create().show()
        }

        val cvBack = findViewById<CardView>(R.id.cvBack)

// Handle tombol kembali menggunakan CardView
        cvBack.setOnClickListener {
            // Memanggil aksi tombol back bawaan Android
            onBackPressedDispatcher.onBackPressed()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
