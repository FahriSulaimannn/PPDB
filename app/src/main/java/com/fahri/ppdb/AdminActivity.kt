package com.fahri.ppdb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/")

    override fun onStart() {
        super.onStart()
        val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
        val userRole = sharedPreferences.getString("user_role", null)

        if (firebaseAuth.currentUser == null || userRole != "admin") {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.admin_activity)

        val runningText = findViewById<TextView>(R.id.runningText)
        runningText.isSelected = true

        val accountImageView = findViewById<ImageView>(R.id.account)

        // Cek apakah user sudah login
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            // Muat gambar profil pengguna ke accountImageView
            val profilePhotoUrl = firebaseUser.photoUrl
            profilePhotoUrl?.let {
                Glide.with(this)
                    .load(it)
                    .placeholder(R.drawable.profile)
                    .circleCrop()
                    .into(accountImageView)
            }
        }

        accountImageView.setOnClickListener {
            val intent = Intent(this, account::class.java)
            startActivity(intent)
        }

        // Mengatur padding untuk sistem bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val EditJadwal: View = findViewById(R.id.EditJadwal)
        EditJadwal.setOnClickListener {
            val intent = Intent(this, BaeritaActivityAdmin::class.java)
            startActivity(intent)
        }

        val cv7: View = findViewById(R.id.Cv7)
        cv7.setOnClickListener {
            checkUserCount { userCount ->
                if (userCount >= 4) {
                    showFullRegistrationDialog()
                } else {
                    val intent = Intent(this, MainActivity4::class.java)
                    startActivity(intent)
                }
            }
        }

        val peringkat: View = findViewById(R.id.peringkat)
        peringkat.setOnClickListener {
            val intent = Intent(this, HasilPeringkatActivity::class.java)
            startActivity(intent)
        }

        val jumlah: View = findViewById(R.id.btnJumlah)
        jumlah.setOnClickListener {
            val intent = Intent(this, SiswaMendaftar::class.java)
            startActivity(intent)
        }

        val cv6: View = findViewById(R.id.Cv6)
        cv6.setOnClickListener {
            val intent = Intent(this, syarat_ketentuan::class.java)
            startActivity(intent)
        }

        val jadwal: View = findViewById(R.id.jadwal)
        jadwal.setOnClickListener {
            val intent = Intent(this, BeritaActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkUserData(uid: String, callback: (Boolean) -> Unit) {
        val userRef = database.getReference("users").child(uid)
        userRef.get().addOnSuccessListener { snapshot ->
            callback(snapshot.exists()) // True jika data ada, false jika tidak
        }.addOnFailureListener {
            // Handle error jika perlu
            callback(false)
        }
    }

    private fun checkUserCount(callback: (Int) -> Unit) {
        val databaseReference = database.getReference("users")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userCount = snapshot.childrenCount.toInt()
                Log.d("DEBUG", "checkUserCount(): userCount = $userCount")
                callback(userCount)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DEBUG", "checkUserCount(): error = ${error.message}")
                callback(0)
            }
        })
    }

    private fun showFullRegistrationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Pendaftaran sudah penuh. Silakan coba lagi nanti.")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Apakah kamu yakin untuk keluar?")
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                finishAffinity()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }
}
