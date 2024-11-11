package com.fahri.ppdb

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity5 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main5)

        // Apply edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize TextViews for each field
        val nama = findViewById<TextView>(R.id.nama)
        val nisn = findViewById<TextView>(R.id.nisn)
        val nik = findViewById<TextView>(R.id.nik)
        val kelamin = findViewById<TextView>(R.id.kelamin)
        val tempatTanggal = findViewById<TextView>(R.id.tempat_tanggal)
        val ortu = findViewById<TextView>(R.id.ortu)
        val alamat = findViewById<TextView>(R.id.alamat)
        val kota = findViewById<TextView>(R.id.kota)
        val telp = findViewById<TextView>(R.id.telp)
        val asalSekolah = findViewById<TextView>(R.id.asal_sekolah)
        val agama = findViewById<TextView>(R.id.agama)
        val indo = findViewById<TextView>(R.id.indo)
        val inggris = findViewById<TextView>(R.id.inggris)
        val matematika = findViewById<TextView>(R.id.matematika)
        val ipa = findViewById<TextView>(R.id.ipa)

        // Get the currently logged-in user's UID
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUID = currentUser?.uid

        if (userUID != null) {
            // Reference to Firebase Database for the logged-in user's data
            val databaseReference = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users").child(userUID)

            // Fetch data from Firebase
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Map Firebase data to each TextView based on the field name
                    nama.text = snapshot.child("name").getValue(String::class.java) ?: "N/A"
                    nisn.text = snapshot.child("nisn").getValue(String::class.java) ?: "N/A"
                    nik.text = snapshot.child("nik").getValue(String::class.java) ?: "N/A"
                    kelamin.text = snapshot.child("gender").getValue(String::class.java) ?: "N/A"
                    tempatTanggal.text = "${snapshot.child("birthPlace").getValue(String::class.java) ?: "N/A"}, ${snapshot.child("birthdate").getValue(String::class.java) ?: "N/A"}"
                    ortu.text = snapshot.child("parentName").getValue(String::class.java) ?: "N/A"
                    alamat.text = snapshot.child("address").getValue(String::class.java) ?: "N/A"
                    kota.text = snapshot.child("city").getValue(String::class.java) ?: "N/A"
                    telp.text = snapshot.child("phone").getValue(String::class.java) ?: "N/A"
                    asalSekolah.text = snapshot.child("schoolOrigin").getValue(String::class.java) ?: "N/A"
                    agama.text = snapshot.child("religion").getValue(String::class.java) ?: "N/A"
                    indo.text = snapshot.child("nilaiIndo").getValue(String::class.java) ?: "N/A"
                    inggris.text = snapshot.child("nilaiIng").getValue(String::class.java) ?: "N/A"
                    matematika.text = snapshot.child("nilaiMat").getValue(String::class.java) ?: "N/A"
                    ipa.text = snapshot.child("nilaiIPA").getValue(String::class.java) ?: "N/A"
                }

                override fun onCancelled(error: DatabaseError) {
                    // jika error
                }
            })
        }
    }
}
