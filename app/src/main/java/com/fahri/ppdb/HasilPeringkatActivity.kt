package com.fahri.ppdb

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fahri.ppdb.databinding.ActivityHasilPeringkatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HasilPeringkatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterPeringkat: AdapterPeringkat
    private lateinit var database: DatabaseReference
    private val itemList = mutableListOf<ModelPeringkat>()
    private lateinit var binding: ActivityHasilPeringkatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cek Android version untuk fitur Edge to Edge
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            enableEdgeToEdge()
        }

        binding = ActivityHasilPeringkatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cek apakah pengguna sudah login
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Harap login terlebih dahulu", Toast.LENGTH_SHORT).show()
            finish() // Kembali ke layar sebelumnya jika belum login
            return
        }

        // Inisialisasi RecyclerView
        recyclerView = binding.recyclerViewPeringkat
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapterPeringkat = AdapterPeringkat(itemList)
        recyclerView.adapter = adapterPeringkat

        // Referensi database dengan user yang login
        database = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("users")

        // Ambil data hanya jika pengguna sudah login
        fetchData()

        // Menambahkan inset untuk sistem bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun fetchData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()
                var index = 1 // Inisialisasi nomor urut
                for (dataSnapshot in snapshot.children) {
                    val name = dataSnapshot.child("name").getValue(String::class.java)
                    val status = dataSnapshot.child("status").getValue(String::class.java)
                    val nilaiIPA = dataSnapshot.child("nilaiIPA").getValue(String::class.java)?.toDoubleOrNull() ?: 0.0
                    val nilaiIndo = dataSnapshot.child("nilaiIndo").getValue(String::class.java)?.toDoubleOrNull() ?: 0.0
                    val nilaiIng = dataSnapshot.child("nilaiIng").getValue(String::class.java)?.toDoubleOrNull() ?: 0.0
                    val nilaiMat = dataSnapshot.child("nilaiMat").getValue(String::class.java)?.toDoubleOrNull() ?: 0.0

                    // Hanya tambahkan ke daftar jika status "approve"
                    if (name != null && status == "approve") {
                        val totalNilai = nilaiIPA + nilaiIndo + nilaiIng + nilaiMat
                        val rataRataNilai = totalNilai / 4
                        val rataRataFormatted = String.format("%.2f", rataRataNilai)

                        // Tambahkan nomor urut sebelum nama
                        val item = ModelPeringkat("$index. $name", rataRataFormatted)
                        itemList.add(item)

                        index++ // Tingkatkan nomor urut
                    }
                }
                adapterPeringkat.updateData(itemList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HasilPeringkatActivity", "Failed to fetch data", error.toException())
            }
        })
    }
}
