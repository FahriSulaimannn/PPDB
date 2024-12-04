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

class MainActivity4 : AppCompatActivity() {

    private val formViewModel: FormViewModel by viewModels()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        }

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users")

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            checkUserData(userId)
        } else {
            // Jika tidak ada user yang login, arahkan ke login page atau logout
            redirectToLogin()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

//    // Fungsi untuk menangani hasil dari pemilihan gambar
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
//            val imageUri = data.data
//            // Temukan Page1Fragment dan set gambar di dalamnya
//            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
//            if (fragment is Page1Fragment) {
//                fragment.view?.findViewById<ImageView>(R.id.image_profile)?.setImageURI(imageUri)
//            }
//        }
//    }

    private fun checkUserData(userId: String) {
        Log.d("DebugCheckUserData", "Checking user data for $userId")
        database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("DebugCheckUserData", "onDataChange triggered")
                if (snapshot.exists()) {
                    Log.d("DebugCheckUserData", "User data found")
                    navigateToFragment(Page3Fragment())
                } else {
                    Log.d("DebugCheckUserData", "User data not found")
                    navigateToFragment(Page1Fragment())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DebugCheckUserData", "Database error: ${error.message}")
                showError("Failed to load user data")
            }
        })
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
        finish() // Menutup MainActivity4
    }

    private fun showError(message: String) {
        // Tampilkan pesan error
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Fungsi untuk navigasi ke halaman Page2Fragment
    fun navigateToPage2() {
        // Mengganti fragment dan menambahkan Page2Fragment ke back stack
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, Page2Fragment())
            .addToBackStack(null) // Menambahkan ke back stack
            .commit()
    }

}
