package com.fahri.ppdb

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        // Ambil data User dari Intent
        val user = intent.getParcelableExtra<User>("user")

        // Tampilkan data ke TextView
        findViewById<TextView>(R.id.nama).text = user?.name ?: "Nama tidak tersedia"
        findViewById<TextView>(R.id.nisn).text = user?.nisn ?: "NISN tidak tersedia"
        findViewById<TextView>(R.id.nik).text = user?.nik ?: "NIK tidak tersedia"
        findViewById<TextView>(R.id.kelamin).text = user?.gender ?: "Jenis kelamin tidak tersedia"
        findViewById<TextView>(R.id.tempat_tanggal).text =
            "${user?.birthPlace ?: "Tempat lahir tidak tersedia"}, ${user?.birthdate ?: "Tanggal lahir tidak tersedia"}"
        findViewById<TextView>(R.id.ortu).text = user?.parentName ?: "Nama orang tua/wali tidak tersedia"
        findViewById<TextView>(R.id.alamat).text = user?.address ?: "Alamat tidak tersedia"
        findViewById<TextView>(R.id.kota).text = user?.city ?: "Kota tidak tersedia"
        findViewById<TextView>(R.id.telp).text = user?.phone ?: "No. Telp tidak tersedia"
        findViewById<TextView>(R.id.asal_sekolah).text = user?.schoolOrigin ?: "Asal sekolah tidak tersedia"
        findViewById<TextView>(R.id.agama).text = user?.religion ?: "Agama tidak tersedia"
        findViewById<TextView>(R.id.indo).text = user?.nilaiIndo?.toString() ?: "Nilai tidak tersedia"
        findViewById<TextView>(R.id.inggris).text = user?.nilaiIng?.toString() ?: "Nilai tidak tersedia"
        findViewById<TextView>(R.id.matematika).text = user?.nilaiMat?.toString() ?: "Nilai tidak tersedia"
        findViewById<TextView>(R.id.ipa).text = user?.nilaiIPA?.toString() ?: "Nilai tidak tersedia"

        // Tampilkan gambar dari Google Drive menggunakan Glide
        val imageViewIds = listOf(
            R.id.imageView1, R.id.imageView2, R.id.imageView3, R.id.imageView4, R.id.imageView5
        )
        val imageLinks = listOfNotNull(user?.driveKK, user?.driveAkta, user?.driveFoto, user?.driveFotoIjazah, user?.driveFotoSertif)

        imageLinks.forEachIndexed { index, link ->
            if (index < imageViewIds.size) {
                val imageView = findViewById<ImageView>(imageViewIds[index])
                loadImageFromDrive(imageView, link)
            }
        }

        // Atur padding untuk window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun loadImageFromDrive(imageView: ImageView, driveLink: String?) {
        if (!driveLink.isNullOrEmpty()) {
            // Ekstrak file ID dari link Google Drive
            val fileId = extractFileId(driveLink)

            // Gunakan thumbnail URL untuk mempercepat
            val thumbnailUrl = "https://drive.google.com/thumbnail?id=$fileId"

            // Opsi Glide untuk optimisasi
            val requestOptions = RequestOptions()
                .override(800, 600) // Resize gambar
                .fitCenter() // Sesuaikan ukuran
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache penuh
                .skipMemoryCache(false) // Cache memori aktif
                .placeholder(R.drawable.proces) // Placeholder
                .error(R.drawable.cancel) // Jika gagal

            // Muat gambar menggunakan Glide
            Glide.with(imageView.context)
                .load(thumbnailUrl)
                .apply(requestOptions)
                .into(imageView)
        } else {
            // Gambar default jika link kosong
            imageView.setImageResource(R.drawable.cancel)
        }
    }


    private fun extractFileId(driveLink: String): String? {
        // Ekstraksi file ID dari link Google Drive
        val regex = "[-\\w]{25,}".toRegex()
        return regex.find(driveLink)?.value
    }
}
