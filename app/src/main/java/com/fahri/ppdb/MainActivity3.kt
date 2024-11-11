package com.fahri.ppdb

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
//import androidx.privacysandbox.tools.core.generator.build
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity3 : AppCompatActivity() {

//    lateinit var textFullName: TextView
//    lateinit var textEmail: TextView
//    lateinit var btnLogout: Button
//    lateinit var googleSignInClient: GoogleSignInClient
//
//    val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main3)

//        textFullName = findViewById(R.id.full_name)
//        textEmail = findViewById(R.id.email)
//        btnLogout = findViewById(R.id.btn_logout)
//
//        val firebaseUser = firebaseAuth.currentUser
//        if (firebaseUser != null) {
//            textFullName.text = firebaseUser.displayName
//            textEmail.text = firebaseUser.email
//        } else {
//            startActivity(Intent(this, LoginActivity::class.java))
//            finish()
//        }
//
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
//
//        btnLogout.setOnClickListener {
//            firebaseAuth.signOut()
//            googleSignInClient.signOut().addOnCompleteListener(this) {
//                googleSignInClient.revokeAccess().addOnCompleteListener(this) {
//                    startActivity(Intent(this, LoginActivity::class.java))
//                    finish()
//                }
//            }
//        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}