package com.fahri.ppdb

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
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

class MainActivity : AppCompatActivity() {

    lateinit var textFullName: TextView
    lateinit var textEmail: TextView
    lateinit var btnLogout: Button
    lateinit var googleSignInClient: GoogleSignInClient

    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        textFullName = findViewById(R.id.full_name)
        textEmail = findViewById(R.id.email)
        btnLogout = findViewById(R.id.btn_logout)

        // Cek apakah pengguna sudah login
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            textFullName.text = firebaseUser.displayName
            textEmail.text = firebaseUser.email
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Pengaturan Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Logout Button
        btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            googleSignInClient.signOut().addOnCompleteListener(this) {
                googleSignInClient.revokeAccess().addOnCompleteListener(this) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }

        // Mengatur padding untuk sistem bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Menambahkan listener untuk CardView Cv7
        val cv7: View = findViewById(R.id.Cv7)
        cv7.setOnClickListener {
            val intent = Intent(this, MainActivity4::class.java)
            startActivity(intent)
        }

        val cv4: View = findViewById(R.id.Cv4)
        cv4.setOnClickListener {
            val intent = Intent(this, MainActivity5::class.java)
            startActivity(intent)
        }
    }

    // Override fungsi onBackPressed untuk konfirmasi keluar
    override fun onBackPressed() {
        super.onBackPressed()
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
