package com.fahri.ppdb

import FormViewModel
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MendaftarAdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mendaftar_admin)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        }

        // Menyesuaikan padding agar tidak terpotong oleh sistem bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Menampilkan fragment_page1_admin saat Activity dimulai
        if (savedInstanceState == null) {
            navigateToFragment(Page1AdminFragment())
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        Log.d("DebugFragmentTransaction", "Navigating to fragment: ${fragment.javaClass.simpleName}")
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        Log.d("DebugFragmentTransaction", "Fragment transaction committed")
    }

    private fun redirectToLogin() {
        // Logika untuk redirect ke login page
        finish() // Menutup MendaftarAdminActivity
    }

    private fun showError(message: String) {
        // Tampilkan pesan error
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Fungsi untuk navigasi ke halaman Page2Fragment
    fun navigateToPage2() {
        // Mengganti fragment dan menambahkan Page2Fragment ke back stack
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, Page2AdminFragment())
            .addToBackStack(null) // Menambahkan ke back stack
            .commit()
    }
}