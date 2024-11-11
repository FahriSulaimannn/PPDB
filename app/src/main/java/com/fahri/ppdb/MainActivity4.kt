package com.fahri.ppdb

import FormViewModel
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class MainActivity4 : AppCompatActivity() {

    private val formViewModel: FormViewModel by viewModels()
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        // Hanya memuat Page1Fragment jika savedInstanceState == null
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Page1Fragment())
                .commit() // Tidak menambahkan Page1Fragment ke back stack
        }

        // Mengatur WindowInsets untuk memberikan padding sesuai sistem bar
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

    // Fungsi untuk navigasi ke halaman Page2Fragment
    fun navigateToPage2() {
        // Mengganti fragment dan menambahkan Page2Fragment ke back stack
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, Page2Fragment())
            .addToBackStack(null) // Menambahkan ke back stack
            .commit()
    }

    // Fungsi untuk menangani klik tombol upload gambar
    fun onUploadImageClick(view: View) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
}
