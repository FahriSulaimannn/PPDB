package com.fahri.ppdb

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
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
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

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

            // Referensi ke database
            val databaseReference = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("admin") // Node admin untuk mendeteksi apakah user adalah admin
            val userId = firebaseUser.uid

            // Periksa apakah user adalah admin
            databaseReference.child(userId).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Jika user adalah admin, ambil nama dari database
                    val adminName = snapshot.child("name").value?.toString()
                    if (!adminName.isNullOrEmpty()) {
                        textFullName.text = adminName // Tampilkan nama admin
                    } else {
                        textFullName.text = "Admin" // Default jika nama admin tidak tersedia
                    }
                } else {
                    // Jika bukan admin, gunakan nama dari Firebase Authentication
                    textFullName.text = firebaseUser.displayName
                }
            }.addOnFailureListener {
                // Gagal mengambil data admin, fallback ke FirebaseAuth
                textFullName.text = firebaseUser.displayName
            }

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
            showLogoutConfirmationDialog()
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
        firebaseUser?.let { user ->
            val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)

            if (googleSignInAccount != null) {
                // Jika akun Google ditemukan, gunakan Google untuk re-autentikasi
                val credential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
                reauthenticateAndDelete(user, credential)
            } else if (user.email != null) {
                // Jika akun menggunakan email, minta pengguna memasukkan kata sandi untuk re-autentikasi
                showPasswordInputDialog(user)
            } else {
                Toast.makeText(this, "Jenis akun tidak dikenali.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPasswordInputDialog(user: FirebaseUser) {
        val passwordInput = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            hint = "Masukkan kata sandi"
        }

        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Kata Sandi")
            .setMessage("Masukkan kata sandi untuk menghapus akun.")
            .setView(passwordInput)
            .setPositiveButton("Konfirmasi") { dialog, _ ->
                val password = passwordInput.text.toString()
                if (password.isNotEmpty()) {
                    val credential = EmailAuthProvider.getCredential(user.email!!, password)
                    reauthenticateAndDelete(user, credential)
                } else {
                    Toast.makeText(this, "Kata sandi tidak boleh kosong.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun reauthenticateAndDelete(user: FirebaseUser, credential: AuthCredential) {
        user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
            if (reauthTask.isSuccessful) {
                // Hapus data pengguna dari Realtime Database
                val databaseReference = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users").child(user.uid)
                databaseReference.removeValue().addOnCompleteListener { dbTask ->
                    if (dbTask.isSuccessful) {
                        // Hapus akun pengguna dari Firebase Authentication
                        user.delete().addOnCompleteListener { deleteTask ->
                            if (deleteTask.isSuccessful) {
                                Toast.makeText(this, "Akun berhasil dihapus dari aplikasi", Toast.LENGTH_SHORT).show()
                                logoutAndClearData()
                            } else {
                                Toast.makeText(this, "Gagal menghapus akun dari Firebase Authentication.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Gagal menghapus data dari Realtime Database.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Re-autentikasi gagal. Silakan coba lagi.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setPositiveButton("Ya") { dialog, _ ->
                logoutAndClearData() // Melakukan logout jika pengguna mengonfirmasi
                dialog.dismiss()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss() // Menutup dialog tanpa logout
            }
            .create()
            .show()
    }


    private fun logoutAndClearData() {
        firebaseAuth.signOut()

        // Revoke Google Sign-In access jika ada
        googleSignInClient.revokeAccess().addOnCompleteListener(this) {
            googleSignInClient.signOut().addOnCompleteListener(this) {
                clearProfileData() // Clear profile data (reset profile image, etc.)
                clearSessionDataExceptDate() // Clear session data, except saved date

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent) // Memulai MainActivity kembali
                finish() // Menutup activity ini
            }
        }
    }

    private fun clearProfileData() {
        textFullName.text = "Nama Pengguna"
        textEmail.text = "Email Pengguna"
        profileImageView.setImageResource(R.drawable.profile) // Set to default profile image
    }

    private fun clearSessionDataExceptDate() {
        // Akses SharedPreferences
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val editor = sharedPref.edit()

        // Ambil data tanggal yang perlu disimpan
        val startDate = sharedPref.getString("start_date", null)
        val endDate = sharedPref.getString("end_date", null)

        // Hapus semua data kecuali data tanggal
        editor.clear()

        // Kembalikan data tanggal ke SharedPreferences
        startDate?.let { editor.putString("start_date", it) }
        endDate?.let { editor.putString("end_date", it) }

        // Kembalikan data lain yang perlu disimpan, seperti saved_date
        val savedDate = sharedPref.getString("saved_date", null)
        savedDate?.let { editor.putString("saved_date", it) }

        editor.apply()
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
