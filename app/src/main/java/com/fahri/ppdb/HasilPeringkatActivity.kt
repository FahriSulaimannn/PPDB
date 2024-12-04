package com.fahri.ppdb

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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

    companion object {
        private const val TOTAL_TARGET = 35 // Jumlah tetap
    }

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
            finish()
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

        val etSearch = binding.etSearch
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val cvBack = findViewById<CardView>(R.id.cvBack)

        // Handle tombol kembali menggunakan CardView
        cvBack.setOnClickListener {
            finish()
        }

        recyclerView.isNestedScrollingEnabled = false

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
                var approvedCount = 0
                var index = 1

                for (dataSnapshot in snapshot.children) {
                    val name = dataSnapshot.child("name").getValue(String::class.java)
                    val status = dataSnapshot.child("status").getValue(String::class.java)
                    val nilaiIPA = dataSnapshot.child("nilaiIPA").getValue(String::class.java)?.toDoubleOrNull() ?: 0.0
                    val nilaiIndo = dataSnapshot.child("nilaiIndo").getValue(String::class.java)?.toDoubleOrNull() ?: 0.0
                    val nilaiIng = dataSnapshot.child("nilaiIng").getValue(String::class.java)?.toDoubleOrNull() ?: 0.0
                    val nilaiMat = dataSnapshot.child("nilaiMat").getValue(String::class.java)?.toDoubleOrNull() ?: 0.0

                    if (name != null) {
                        if (status == "approve") {
                            approvedCount++
                            val totalNilai = nilaiIPA + nilaiIndo + nilaiIng + nilaiMat
                            val rataRataNilai = totalNilai / 4
                            val rataRataFormatted = String.format("%.2f", rataRataNilai)

                            val item = ModelPeringkat("$index. $name", " Nilai : $rataRataFormatted")
                            itemList.add(item)
                            index++
                        }
                    }
                }

                // Perbarui data di adapter
                adapterPeringkat.updateData(itemList)

                // Perbarui informasi header di TextView
                val approveCountText = "Jumlah Pendaftar : $approvedCount/$TOTAL_TARGET"
                binding.jumlah.text = approveCountText

                // Tampilkan/hilangkan pesan jika tidak ada data
                if (itemList.isEmpty()) {
                    binding.recyclerViewPeringkat.visibility = View.GONE
                    binding.tvNoData.visibility = View.VISIBLE
                } else {
                    binding.recyclerViewPeringkat.visibility = View.VISIBLE
                    binding.tvNoData.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HasilPeringkatActivity", "Failed to fetch data", error.toException())
            }
        })
    }

    private fun filterList(query: String) {
        val filteredList = itemList.filter {
            it.name?.contains(query, ignoreCase = true) == true
        }
        adapterPeringkat.updateData(filteredList)
    }
}
