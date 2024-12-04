package com.fahri.ppdb

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import android.content.res.Configuration
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView

class BeritaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdapterBerita
    private lateinit var database: DatabaseReference
    private lateinit var teks: TextView
    private val dataList = mutableListOf<ModelBerita>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_berita)

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recyclerViewBerita)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdapterBerita(dataList)
        recyclerView.adapter = adapter

        // Inisialisasi Firebase Realtime Database
        database = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("berita")

        // Memuat data dari database
        loadDataFromDatabase()

        teks = findViewById(R.id.tvEmptyMessage)

//        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewBerita)
//        recyclerView.layoutManager = object : LinearLayoutManager(this) {
//            override fun canScrollVertically(): Boolean {
//                return false // Disable vertical scrolling
//            }
//
//            override fun canScrollHorizontally(): Boolean {
//                return false // Disable horizontal scrolling (if needed)
//            }
//        }

        recyclerView.isNestedScrollingEnabled = false

        val cvBack = findViewById<CardView>(R.id.cvBack)

        // Handle tombol kembali menggunakan CardView
        cvBack.setOnClickListener {
            finish() // Menutup activity sepenuhnya
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadDataFromDatabase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Kosongkan daftar untuk mencegah duplikasi
                dataList.clear()

                // Iterasi setiap data dalam database
                for (dataSnapshot in snapshot.children) {
                    val berita = dataSnapshot.getValue(ModelBerita::class.java)
                    if (berita != null) {
                        dataList.add(berita)
                    }
                }

                // Perbarui adapter setelah data dimuat
                adapter.notifyDataSetChanged()

                // Atur visibilitas pesan kosong
                val isDataEmpty = dataList.isEmpty()
                teks.visibility = if (isDataEmpty) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Database error: ${error.message}")
                Toast.makeText(this@BeritaActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

}