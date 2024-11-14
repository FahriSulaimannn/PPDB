package com.fahri.ppdb

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import androidx.cardview.widget.CardView
import com.google.firebase.auth.UserProfileChangeRequest

class account : ComponentActivity() {

    private lateinit var textFullName: TextView
    private lateinit var textEmail: TextView
    private lateinit var btnLogout: Button
    private lateinit var btnChangePassword: Button
    private lateinit var btnDeleteAccount: Button
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var editProfile: Button
    private lateinit var profileImageView: ImageView
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account)

        textFullName = findViewById(R.id.full_name)
        textEmail = findViewById(R.id.email)
        btnLogout = findViewById(R.id.btn_logout)
        btnChangePassword = findViewById(R.id.btn_change_password)
        btnDeleteAccount = findViewById(R.id.btn_delete_account)
        editProfile = findViewById(R.id.btn_edit_profile)
        profileImageView = findViewById(R.id.profile)
        val cvBack = findViewById<CardView>(R.id.backButton)

        // Back button function
        cvBack.setOnClickListener {
            onBackPressed()
        }

        // Load user profile
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            loadUserProfile(firebaseUser)
            textFullName.text = firebaseUser.displayName
            textEmail.text = firebaseUser.email

            // Check if user logged in with email and password
            val isEmailUser = firebaseUser.providerData.any { it.providerId == EmailAuthProvider.PROVIDER_ID }
            btnChangePassword.isEnabled = isEmailUser
            if (!isEmailUser) {
                btnChangePassword.visibility = Button.GONE
            }
        } else {
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
            logoutAndClearData()
        }

        // Edit profile dialog
        editProfile.setOnClickListener {
            showEditProfileDialog(firebaseUser)
        }

        // Change password dialog
        btnChangePassword.setOnClickListener {
            showChangePasswordDialog(firebaseUser)
        }

        // Delete account button click listener
        btnDeleteAccount.setOnClickListener {
            showDeleteAccountConfirmation(firebaseUser)
        }

        // Adjust UI for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadUserProfile(firebaseUser: FirebaseUser) {
        val profilePhotoUrl = firebaseUser.photoUrl
        profilePhotoUrl?.let {
            Glide.with(this)
                .load(it)
                .placeholder(R.drawable.profile)
                .circleCrop()
                .into(profileImageView)
        }
    }

    private fun showDeleteAccountConfirmation(firebaseUser: FirebaseUser?) {
        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus Akun")
            .setMessage("Apakah kamu yakin ingin menghapus akun ini secara permanen?")
            .setPositiveButton("Ya") { dialog, _ ->
                deleteFirebaseAccount(firebaseUser)
                dialog.dismiss()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }

        dialogBuilder.create().show()
    }

    private fun deleteFirebaseAccount(firebaseUser: FirebaseUser?) {
        firebaseUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Akun berhasil dihapus dari aplikasi", Toast.LENGTH_SHORT).show()
                logoutAndClearData()
            } else {
                Toast.makeText(this, "Gagal menghapus akun. Hanya akun aplikasi yang dihapus.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun logoutAndClearData() {
        firebaseAuth.signOut()

        // Revoke Google Sign-In access
        googleSignInClient.revokeAccess().addOnCompleteListener(this) {
            googleSignInClient.signOut().addOnCompleteListener(this) {
                // Clear profile data
                clearProfileData()

                // Redirect to login screen
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun clearProfileData() {
        textFullName.text = "Nama Pengguna"
        textEmail.text = "Email Pengguna"
        profileImageView.setImageResource(R.drawable.profile)
    }

    private fun showChangePasswordDialog(firebaseUser: FirebaseUser?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null)
        val editOldPassword = dialogView.findViewById<EditText>(R.id.edit_old_password)
        val editNewPassword = dialogView.findViewById<EditText>(R.id.edit_new_password)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Ganti Password")
            .setPositiveButton("Simpan") { dialog, _ ->
                val oldPassword = editOldPassword.text.toString()
                val newPassword = editNewPassword.text.toString()

                if (oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                    val email = firebaseUser?.email
                    if (email != null) {
                        val credential = EmailAuthProvider.getCredential(email, oldPassword)
                        firebaseUser.reauthenticate(credential).addOnCompleteListener { authTask ->
                            if (authTask.isSuccessful) {
                                firebaseUser.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        Toast.makeText(this, "Password berhasil diperbarui", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this, "Gagal memperbarui password", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(this, "Password lama salah", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

        dialogBuilder.create().show()
    }

    private fun showEditProfileDialog(firebaseUser: FirebaseUser?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null)
        val editName = dialogView.findViewById<EditText>(R.id.edit_name)
        val editProfileImage = dialogView.findViewById<ImageView>(R.id.edit_profile_image)

        editName.setText(firebaseUser?.displayName)
        firebaseUser?.photoUrl?.let {
            Glide.with(this)
                .load(it)
                .circleCrop()
                .into(editProfileImage)
        }

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Edit Profile")
            .setPositiveButton("Save") { dialog, _ ->
                val newName = editName.text.toString()
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build()

                firebaseUser?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        textFullName.text = newName
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        dialogBuilder.create().show()
    }
}
