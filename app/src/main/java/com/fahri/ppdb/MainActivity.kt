package com.fahri.ppdb

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/")

//    lateinit var textFullName: TextView
//    lateinit var textEmail: TextView
//    lateinit var btnLogout: Button
//    lateinit var googleSignInClient: GoogleSignInClient
//
//    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

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
                    .placeholder(R.drawable.profile) // gambar default jika tidak ada foto profil
                    .circleCrop() // buat gambar bulat
                    .into(accountImageView)
            }
        }

        accountImageView.setOnClickListener {
            val intent = Intent(this, account::class.java)
            startActivity(intent)
        }

//        textFullName = findViewById(R.id.full_name)
//        textEmail = findViewById(R.id.email)
//        btnLogout = findViewById(R.id.btn_logout)

        // Cek apakah pengguna sudah login
//        val firebaseUser = firebaseAuth.currentUser
//        if (firebaseUser != null) {
//            textFullName.text = firebaseUser.displayName
//            textEmail.text = firebaseUser.email
//        } else {
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//        }
//
//        // Pengaturan Google Sign-In
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
//
//        // Logout Button
//        btnLogout.setOnClickListener {
//            firebaseAuth.signOut()
//            googleSignInClient.signOut().addOnCompleteListener(this) {
//                googleSignInClient.revokeAccess().addOnCompleteListener(this) {
//                    startActivity(Intent(this, LoginActivity::class.java))
//                    finish()
//                }
//            }
//        }

        // Mengatur padding untuk sistem bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Mengatur view dan listener
        val cv7: View = findViewById(R.id.Cv7)
        cv7.setOnClickListener {
            // Debug untuk mengetahui apakah onClick dijalankan
            Log.d("DEBUG", "cv7 button clicked")

            checkUserCount { userCount ->
                val firebaseUser = firebaseAuth.currentUser

                if (firebaseUser == null) {
                    // Debug user belum login
                    Log.d("DEBUG", "User belum login")

                    if (userCount >= 4) {
                        // Data penuh
                        Log.d("DEBUG", "Data penuh, menampilkan dialog pendaftaran penuh")
                        showFullRegistrationDialog()
                    } else {
                        // Data masih bisa diisi
                        Log.d("DEBUG", "Data tersedia, menampilkan dialog login")
                        showLoginDialog()
                    }
                } else {
                    // Debug user sudah login
                    Log.d("DEBUG", "User sudah login")

                    if (userCount >= 4) {
                        // Data penuh
                        checkUserData(firebaseUser.uid) { hasData ->
                            if (hasData) {
                                // Sudah mendaftar
                                Log.d("DEBUG", "User sudah mendaftar, menuju MainActivity4")
                                navigateToMainActivity4()
                            } else {
                                // Belum mendaftar
                                Log.d("DEBUG", "User belum mendaftar, menampilkan dialog pendaftaran penuh")
                                showFullRegistrationDialog()
                            }
                        }
                    } else {
                        // Data masih bisa diisi
                        checkUserData(firebaseUser.uid) { hasData ->
                            if (hasData) {
                                // Sudah mendaftar
                                Log.d("DEBUG", "User sudah mendaftar, menuju MainActivity4")
                                navigateToMainActivity4()
                            } else {
                                // Belum mendaftar
                                Log.d("DEBUG", "User belum mendaftar, menuju MainActivity4")
                                navigateToMainActivity4()
                            }
                        }
                    }
                }
            }
        }


        val cv4: View = findViewById(R.id.Cv4)
        cv4.setOnClickListener {
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                val intent = Intent(this, MainActivity5::class.java)
                startActivity(intent)
            } else {
                showLoginDialog()
            }
        }

        val cv5: View = findViewById(R.id.Cv5)
        cv5.setOnClickListener {
            val intent = Intent(this, tatacara::class.java)
            startActivity(intent)
        }

        val peringkat: View = findViewById(R.id.peringkat)
        peringkat.setOnClickListener {
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                val intent = Intent(this, HasilPeringkatActivity::class.java)
                startActivity(intent)
            } else {
                showLoginDialog()
            }
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


        // Dalam Activity atau Fragment
//        val accountImageView = findViewById<ImageView>(R.id.account)
//
//        accountImageView.setOnClickListener {
//            val intent = Intent(this, account::class.java)
//            startActivity(intent)
//        }

    }

    private fun navigateToMainActivity4() {
        val intent = Intent(this, MainActivity4::class.java)
        startActivity(intent)
    }

    // Fungsi untuk memeriksa jumlah pengguna
    private fun checkUserCount(callback: (Int) -> Unit) {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            Log.e("DEBUG", "Pengguna tidak terautentikasi")
            callback(0) // Pengguna belum terautentikasi
            return
        }

        val databaseReference = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userCount = snapshot.childrenCount.toInt()
                Log.d("DEBUG", "checkUserCount(): userCount = $userCount")
                callback(userCount)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DEBUG", "checkUserCount(): error = ${error.message}")
                callback(0) // Default jika terjadi error
            }
        })
    }




    // Fungsi untuk memeriksa apakah pengguna sudah memiliki data di Firebase
    private fun checkUserData(uid: String, callback: (Boolean) -> Unit) {
        val userRef = database.getReference("users").child(uid)
        userRef.get().addOnSuccessListener { snapshot ->
            callback(snapshot.exists()) // True jika data ada, false jika tidak
        }.addOnFailureListener {
            // Handle error jika perlu
            callback(false)
        }
    }


    // Dialog jika pendaftaran sudah penuh
    private fun showFullRegistrationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Pendaftaran sudah penuh. Silakan coba lagi nanti.")
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }


    // Dialog untuk meminta login
    private fun showLoginDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Anda perlu login terlebih dahulu untuk mendaftar.")
            .setCancelable(false)
            .setPositiveButton("Login") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    // Override fungsi onBackPressed untuk konfirmasi keluar
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Apakah kamu yakin untuk keluar?")
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                // Keluar dari aplikasi sepenuhnya
                finishAffinity()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                // Tutup dialog dan tetap di aplikasi
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }



}

