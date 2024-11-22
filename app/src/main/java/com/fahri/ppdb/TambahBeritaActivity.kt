package com.fahri.ppdb

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TambahBeritaActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_berita)

        // Inisialisasi Firebase
        database = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("berita")

        // Tombol Simpan
        findViewById<Button>(R.id.btnSimpanBaru).setOnClickListener {
            val judul = findViewById<EditText>(R.id.editJudulBaru).text.toString().trim()
            val isi = findViewById<EditText>(R.id.editIsiBaru).text.toString().trim()
            val lokasi = findViewById<EditText>(R.id.editLokasiBaru).text.toString().trim()
            val tanggal = findViewById<EditText>(R.id.editTanggalBaru).text.toString().trim()

            if (judul.isEmpty() || isi.isEmpty() || tanggal.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                // Buat ID unik untuk berita
                val beritaId = database.push().key ?: return@setOnClickListener

                // Data yang akan disimpan
                val berita = mapOf(
                    "judul" to judul,
                    "isi" to isi,
                    "lokasi" to lokasi,
                    "tanggal" to tanggal
                )

                // Simpan ke Firebase
                database.child(beritaId).setValue(berita).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Berita berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        finish() // Kembali ke activity sebelumnya
                    } else {
                        Toast.makeText(this, "Gagal menambahkan berita", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
