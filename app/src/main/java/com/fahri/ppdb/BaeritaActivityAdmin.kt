package com.fahri.ppdb

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class BaeritaActivityAdmin : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterBeritaAdmin
    private lateinit var database: DatabaseReference
    private lateinit var teks: TextView
    private val dataList = mutableListOf<ModelBeritaAdmin>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_baerita_admin)

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recyclerViewBerita)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AdapterBeritaAdmin(dataList) { berita ->
            val intent = Intent(this, EditBeritaActivity::class.java)
            intent.putExtra("beritaId", berita.id) // Kirim ID berita ke Activity Edit
            intent.putExtra("judul", berita.judul)
            intent.putExtra("isi", berita.isi)
            intent.putExtra("lokasi", berita.lokasi)
            intent.putExtra("tanggal", berita.tanggal)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Inisialisasi Firebase Realtime Database
        database = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("berita")

        // Memuat data dari database
        loadDataFromDatabase()

        findViewById<FloatingActionButton>(R.id.fabAddSchedule).setOnClickListener {
            val intent = Intent(this, TambahBeritaActivity::class.java)
            startActivity(intent)
        }

        recyclerView.isNestedScrollingEnabled = false

        val cvBack = findViewById<CardView>(R.id.cvBack)

        // Handle tombol kembali menggunakan CardView
        cvBack.setOnClickListener {
            finish() // Menutup activity sepenuhnya
        }

        teks = findViewById(R.id.tvEmptyMessage)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadDataFromDatabase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()

                for (dataSnapshot in snapshot.children) {
                    val berita = dataSnapshot.getValue(ModelBeritaAdmin::class.java)
                    if (berita != null) {
                        berita.id = dataSnapshot.key.orEmpty() // Set kunci (id) dari Firebase
                        dataList.add(berita)
                    } else {
                        Log.w("BeritaActivityAdmin", "Data tidak valid: ${dataSnapshot.value}")
                    }
                }

                adapter.notifyDataSetChanged()

                // Atur visibilitas pesan kosong
                val isDataEmpty = dataList.isEmpty()
                teks.visibility = if (isDataEmpty) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("BeritaActivityAdmin", "Database error: ${error.message}")
                Toast.makeText(this@BaeritaActivityAdmin, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

}