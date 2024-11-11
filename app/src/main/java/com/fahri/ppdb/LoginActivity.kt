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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

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
                startActivity(Intent(this, MainActivity::class.java))
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener{
                progresDialog.dismiss()
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
        val credentian = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credentian)
            .addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener{
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
