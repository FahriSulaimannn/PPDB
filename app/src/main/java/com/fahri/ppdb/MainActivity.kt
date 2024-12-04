package com.fahri.ppdb

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
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
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/")

//    lateinit var textFullName: TextView
//    lateinit var textEmail: TextView
//    lateinit var btnLogout: Button
//    lateinit var googleSignInClient: GoogleSignInClient
//
//    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onStart() {
        super.onStart()
        val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
        val userRole = sharedPreferences.getString("user_role", "user")

        // Periksa apakah user adalah admin
        if (firebaseAuth.currentUser != null && userRole == "admin") {
            startActivity(Intent(this, AdminActivity::class.java))
            finish()
        }

        // Jika user tidak login, tetap di MainActivity, biarkan mereka eksplorasi fitur umum
        // Fitur yang memerlukan login akan meminta login ketika diakses
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val runningText = findViewById<TextView>(R.id.runningText)
        runningText.isSelected = true

        val accountImageView = findViewById<ImageView>(R.id.account)

        val greetingTextView = findViewById<TextView>(R.id.greeting)

        // Cek apakah user sudah login
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            // Muat gambar profil pengguna ke accountImageView
            greetingTextView.text = firebaseUser.displayName
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

        val chat: View = findViewById(R.id.chat)
        chat.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://wa.me/6285607404143")
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

        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val startDateString = sharedPreferences.getString("start_date", null)
        val endDateString = sharedPreferences.getString("end_date", null)

        Log.d("SharedPreferences", "Start Date: $startDateString, End Date: $endDateString")

        val startDate = startDateString?.let { LocalDate.parse(it) }
        val endDate = endDateString?.let { LocalDate.parse(it) }

        val currentDate = LocalDate.now()
        val cv7: View = findViewById(R.id.Cv7) // Tombol atau View yang akan diaktifkan/dinonaktifkan

// Tombol tetap diaktifkan, hanya beri efek jika tanggal di luar rentang
        if (startDate != null && endDate != null) {
            if (currentDate !in startDate..endDate) {
                cv7.alpha = 0.5f // Ubah transparansi tombol
                cv7.setOnClickListener {
                    showRegistrationClosedDialog() // Tampilkan dialog jika pendaftaran sudah tutup
                }
            } else {
                cv7.alpha = 1f // Kembalikan transparansi tombol normal
                cv7.setOnClickListener {
                    // Proses normal ketika tanggal masih dalam rentang
                    handleButtonClick()
                }
            }
        } else {
            cv7.alpha = 0.5f // Ubah transparansi tombol jika tidak ada tanggal
            cv7.setOnClickListener {
                showRegistrationClosedDialog() // Tampilkan dialog jika pendaftaran sudah tutup
            }
        }

        val cvJ: View = findViewById(R.id.cardJadwal)
        cvJ.setOnClickListener {
            val intent = Intent(this, BeritaActivity::class.java)
            startActivity(intent)
        }

        val cvS: View = findViewById(R.id.cardSyarat)
        cvS.setOnClickListener {
            val intent = Intent(this, syarat_ketentuan::class.java)
            startActivity(intent)
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

        val Alamat: View = findViewById(R.id.lokasi)
        Alamat.setOnClickListener {
            val intent = Intent(this, AlamatActivity::class.java)
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

    private fun showRegistrationClosedDialog() {
        // Menampilkan dialog untuk memberitahukan bahwa pendaftaran sudah tutup
        AlertDialog.Builder(this)
            .setTitle("Pendaftaran Tutup")
            .setMessage("Pendaftaran sudah tutup. Mohon tunggu periode pendaftaran berikutnya.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun handleButtonClick() {
        // Menangani logika normal untuk tombol
        checkUserCount { userCount ->
            val firebaseUser = firebaseAuth.currentUser

            if (firebaseUser == null) {
                // User belum login
                Log.d("DEBUG", "User belum login")
                if (userCount >= 35) {
                    showFullRegistrationDialog()
                } else {
                    showLoginDialog()
                }
            } else {
                // User sudah login
                Log.d("DEBUG", "User sudah login")
                if (userCount >= 35) {
                    checkUserData(firebaseUser.uid) { hasData ->
                        if (hasData) {
                            navigateToMainActivity4()
                        } else {
                            showFullRegistrationDialog()
                        }
                    }
                } else {
                    checkUserData(firebaseUser.uid) { hasData ->
                        if (hasData) {
                            navigateToMainActivity4()
                        } else {
                            navigateToMainActivity4()
                        }
                    }
                }
            }
        }
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

