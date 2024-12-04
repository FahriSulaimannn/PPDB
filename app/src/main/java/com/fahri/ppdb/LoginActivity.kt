package com.fahri.ppdb

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    lateinit var editEmail: EditText
    lateinit var editPassword: EditText
//    lateinit var btnRegister: Button
    lateinit var btnLogin: Button
    lateinit var btnGoogle: SignInButton
    lateinit var txtRegister: TextView
    lateinit var googleSignInClient: GoogleSignInClient

    lateinit var progresDialog: ProgressDialog

    var firebaseAuth = FirebaseAuth.getInstance()

    companion object{
        private const val RC_SIGN_IN = 1001
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login2)

        editEmail = findViewById(R.id.email)
        editPassword = findViewById(R.id.password)
//        btnRegister = findViewById(R.id.btn_register)
        btnLogin = findViewById(R.id.btn_login)
        btnGoogle = findViewById(R.id.btn_google)
        txtRegister = findViewById(R.id.txt_register)

        progresDialog = ProgressDialog(this)
        progresDialog.setTitle("Logging")
        progresDialog.setMessage("Silahkan tunggu")

        val txtForgotPassword: TextView = findViewById(R.id.txtForgotPassword)

        (btnGoogle.getChildAt(0) as? TextView)?.text = "Login dengan Google"

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        txtForgotPassword.setOnClickListener {
            val email = editEmail.text.toString()
            if (email.isNotEmpty()) {
                progresDialog.setMessage("Mengirim email reset password...")
                progresDialog.show()

                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        progresDialog.dismiss()
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Email reset password telah dikirim.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Gagal mengirim email reset password. Periksa email yang Anda masukkan.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Silakan masukkan email terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogin.setOnClickListener {
            if (editEmail.text.isNotEmpty() && editPassword.text.isNotEmpty()) {
                procesLogin()
            }else {
                Toast.makeText(this, "Silahkan isi email dan password terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }

        btnGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

//        btnRegister.setOnClickListener{
//            startActivity(Intent(this, RegisterActivity::class.java))
//        }

        txtRegister.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

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

    private fun procesLogin() {
        val email = editEmail.text.toString()
        val password = editPassword.text.toString()

        progresDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val currentUser = firebaseAuth.currentUser
                if (currentUser != null) {
                    val uid = currentUser.uid
                    val adminRef = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("admin").child(uid)

                    adminRef.get().addOnCompleteListener { task ->
                        progresDialog.dismiss()
                        if (task.isSuccessful) {
                            val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()

                            if (task.result.exists()) {
                                // Admin
                                editor.putString("user_role", "admin")
                                editor.apply()
                                Toast.makeText(this, "Login sebagai Admin", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, AdminActivity::class.java))
                            } else {
                                // User
                                editor.putString("user_role", "user")
                                editor.apply()
                                Toast.makeText(this, "Login sebagai User", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                            }
                            finish()
                        } else {
                            Toast.makeText(this, "Gagal memeriksa role pengguna.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener { error ->
                progresDialog.dismiss()
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }


    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException){
            e.printStackTrace()
            Toast.makeText(applicationContext, e.localizedMessage, LENGTH_SHORT).show()
        }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        progresDialog.show()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                val currentUser = firebaseAuth.currentUser
                if (currentUser != null) {
                    // Simpan role sebagai "user" dalam SharedPreferences
                    val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
                    sharedPreferences.edit().putString("user_role", "user").apply()

                    // Arahkan ke MainActivity
                    Toast.makeText(this, "Login berhasil sebagai User", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener { error ->
                progresDialog.dismiss()
                Toast.makeText(this, "Login dengan Google gagal: ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                progresDialog.dismiss()
            }
    }


    private fun logout() {
        googleSignInClient.signOut().addOnCompleteListener(this) {
            googleSignInClient.revokeAccess().addOnCompleteListener(this) {
                // Navigasi ke halaman login atau halaman lain yang sesuai
                startActivity(Intent(this, LoginActivity::class.java))
                finish() // Menutup LoginActivity agar tidak kembali ke halaman sebelumnya
            }
        }
    }
}
