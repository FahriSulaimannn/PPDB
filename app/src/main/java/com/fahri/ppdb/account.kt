package com.fahri.ppdb

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.bumptech.glide.Glide
import android.widget.ImageView
import androidx.cardview.widget.CardView

class account : ComponentActivity() {

    lateinit var textFullName: TextView
    lateinit var textEmail: TextView
    lateinit var btnLogout: Button
    lateinit var googleSignInClient: GoogleSignInClient

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)

        textFullName = findViewById(R.id.full_name)
        textEmail = findViewById(R.id.email)
        btnLogout = findViewById(R.id.btn_logout)

        val cvBack = findViewById<CardView>(R.id.backButton)

        cvBack.setOnClickListener {
            onBackPressed()  // Kembali ke halaman sebelumnya
        }

        val profileImageView: ImageView = findViewById(R.id.profile)

        // Check if the user is logged in with Firebase Auth
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {

            val profilePhotoUrl = firebaseUser.photoUrl

            // Menggunakan Glide untuk memuat gambar dari URL ke dalam ImageView
            profilePhotoUrl?.let {
                Glide.with(this)
                    .load(it)
                    .placeholder(R.drawable.profile) // gambar default
                    .circleCrop() // Transformasi gambar menjadi lingkaran
                    .into(profileImageView)
            }

            // Display user information if logged in
            textFullName.text = firebaseUser.displayName
            textEmail.text = firebaseUser.email
        } else {
            // Redirect to login if no user is logged in
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Configure Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Logout Button click listener
        btnLogout.setOnClickListener {
            firebaseAuth.signOut() // Sign out from Firebase
            googleSignInClient.signOut().addOnCompleteListener(this) {
                googleSignInClient.revokeAccess().addOnCompleteListener(this) {
                    // Redirect to login screen after logout
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
        }

        // Adjust UI to accommodate system bars (edge-to-edge layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
