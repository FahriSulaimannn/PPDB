package com.fahri.ppdb

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity5 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main5)

        // Apply edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize TextViews for each field
        val nama = findViewById<TextView>(R.id.nama)
        val nisn = findViewById<TextView>(R.id.nisn)
        val nik = findViewById<TextView>(R.id.nik)
        val kelamin = findViewById<TextView>(R.id.kelamin)
        val tempatTanggal = findViewById<TextView>(R.id.tempat_tanggal)
        val ortu = findViewById<TextView>(R.id.ortu)
        val alamat = findViewById<TextView>(R.id.alamat)
        val kota = findViewById<TextView>(R.id.kota)
        val telp = findViewById<TextView>(R.id.telp)
        val asalSekolah = findViewById<TextView>(R.id.asal_sekolah)
        val agama = findViewById<TextView>(R.id.agama)
        val indo = findViewById<TextView>(R.id.indo)
        val inggris = findViewById<TextView>(R.id.inggris)
        val matematika = findViewById<TextView>(R.id.matematika)
        val ipa = findViewById<TextView>(R.id.ipa)
        val cvBack = findViewById<CardView>(R.id.cvBack)

        cvBack.setOnClickListener {
            onBackPressed()  // Kembali ke halaman sebelumnya
        }

        // Download PDF button
        val downloadPdfButton = findViewById<Button>(R.id.download_pdf)
        downloadPdfButton.setOnClickListener {
            generatePdf(
                nama.text.toString(), nisn.text.toString(), nik.text.toString(),
                kelamin.text.toString(), tempatTanggal.text.toString(),
                ortu.text.toString(), alamat.text.toString(), kota.text.toString(),
                telp.text.toString(), asalSekolah.text.toString(), agama.text.toString(),
                indo.text.toString(), inggris.text.toString(), matematika.text.toString(), ipa.text.toString()
            )
        }

        // Get the currently logged-in user's UID
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userUID = currentUser?.uid

        if (userUID != null) {
            val databaseReference = FirebaseDatabase.getInstance("https://coba-2db4c-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("users").child(userUID)

            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    nama.text = snapshot.child("name").getValue(String::class.java) ?: "N/A"
                    nisn.text = snapshot.child("nisn").getValue(String::class.java) ?: "N/A"
                    nik.text = snapshot.child("nik").getValue(String::class.java) ?: "N/A"
                    kelamin.text = snapshot.child("gender").getValue(String::class.java) ?: "N/A"
                    tempatTanggal.text = "${snapshot.child("birthPlace").getValue(String::class.java) ?: "N/A"}, ${snapshot.child("birthdate").getValue(String::class.java) ?: "N/A"}"
                    ortu.text = snapshot.child("parentName").getValue(String::class.java) ?: "N/A"
                    alamat.text = snapshot.child("address").getValue(String::class.java) ?: "N/A"
                    kota.text = snapshot.child("city").getValue(String::class.java) ?: "N/A"
                    telp.text = snapshot.child("phone").getValue(String::class.java) ?: "N/A"
                    asalSekolah.text = snapshot.child("schoolOrigin").getValue(String::class.java) ?: "N/A"
                    agama.text = snapshot.child("religion").getValue(String::class.java) ?: "N/A"
                    indo.text = snapshot.child("nilaiIndo").getValue(String::class.java) ?: "N/A"
                    inggris.text = snapshot.child("nilaiIng").getValue(String::class.java) ?: "N/A"
                    matematika.text = snapshot.child("nilaiMat").getValue(String::class.java) ?: "N/A"
                    ipa.text = snapshot.child("nilaiIPA").getValue(String::class.java) ?: "N/A"
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle possible errors
                }
            })
        }
    }

    private fun generatePdf(nama: String, nisn: String, nik: String, kelamin: String,
                            tempatTanggal: String, ortu: String, alamat: String, kota: String,
                            telp: String, asalSekolah: String, agama: String, indo: String,
                            inggris: String, matematika: String, ipa: String) {

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Ukuran A4
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply { textSize = 12f }

        // Lebar halaman PDF
        val pageWidth = pageInfo.pageWidth

        // Judul dan Sub Judul
        paint.textSize = 16f
        paint.isFakeBoldText = true

        // Mengatur posisi X agar teks berada di tengah halaman
        val title1 = "SURAT PERNYATAAN SISWA"
        val title2 = "SMA Kurnia Abadi Surakarta"
        val title1X = (pageWidth - paint.measureText(title1)) / 2
        val title2X = (pageWidth - paint.measureText(title2)) / 2

        // Gambar teks judul di tengah
        canvas.drawText(title1, title1X, 50f, paint)
        canvas.drawText(title2, title2X, 70f, paint)

        // Atur posisi teks awal untuk teks di atas data
        paint.textSize = 14f
        paint.isFakeBoldText = true
        var yPosition = 90f  // Ubah posisi awal agar teks ini berada di atas data

        // Tambahkan jarak pada atas teks
        yPosition += 10f  // Menambahkan jarak 10f pada bagian atas teks

// Tambahkan teks "abcubuabcuioabcouabcoabcoabc"
        canvas.drawText("Saya yang bertanda tangan dibawah ini, siswa SMK Kurnia Abadi Surakarta : ", 40f, yPosition, paint)

// Tambahkan jarak antara teks tambahan dan data
        yPosition += 15f

// Konten Formulir (data)
        paint.textSize = 12f
        paint.isFakeBoldText = false
        val data = mapOf(
            "1. Nama                    " to nama,
            "2. Tempat / Tgl. Lahir     " to tempatTanggal,
            "5. Jenis Kelamin           " to kelamin,
            "7. Alamat Lengkap          " to alamat,
            "9. Nomor Telepon / HP      " to telp,
            "11. Agama                  " to agama
        )

// Atur posisi teks masing-masing item
        for ((key, value) in data) {
            canvas.drawText("$key : $value", 40f, yPosition, paint)
            yPosition += 20f
        }


        // Setel gaya teks menjadi bold
        paint.isFakeBoldText = true
        paint.textSize = 14f

// Bagian menyatakan dan penandatangan
        yPosition += 30f
        val statementText = "Menyatakan:"
        val statementWidth = paint.measureText(statementText)  // Menghitung panjang teks
        val statementX = (canvas.width - statementWidth) / 2  // Menghitung posisi x agar teks berada di tengah

        canvas.drawText(statementText, statementX, yPosition, paint)
        yPosition += 20f

// Daftar pernyataan
        val pernyataanText = listOf(
            "1. Semua data yang ditulis adalah benar.",
            "2. Akan belajar dengan tekun dan penuh semangat.",
            "3. Menjaga nama baik diri sendiri, keluarga, dan sekolah.",
            "4. Bersedia mengikuti pelajaran Pendidikan Agama Kristen dan kegiatan ibadah.",
            "5. Sanggup menaati dan mematuhi Tata Tertib Siswa."
        )

// Menggambar setiap pernyataan di tengah
        for (statement in pernyataanText) {
            val statementWidth = paint.measureText(statement)  // Menghitung panjang teks pernyataan
            val statementX = (canvas.width - statementWidth) / 2  // Menghitung posisi x untuk menempatkan teks di tengah
            canvas.drawText(statement, statementX, yPosition, paint)
            yPosition += 20f
        }

// Setelah menggambar data, buat jarak untuk kalimat baru
        yPosition += 40f  // Memberikan jarak antara bagian data dan kalimat baru

// Menambahkan kalimat "qwertyuiopasdfghjkl" di kiri, di luar bagian data
        val kalimatBaru = "Demikian surat pernyataan ini kami perbuat dengan sebenarnya dan dalam keadaan waras."
        canvas.drawText(kalimatBaru, 40f, yPosition, paint)  // Ini tetap di kiri


        // Tanda tangan
        yPosition += 40f
        canvas.drawText("Diketahui Oleh,", 40f, yPosition, paint)
        canvas.drawText("Kurnia Abadi, _________", 400f, yPosition, paint)
        yPosition += 20f
        canvas.drawText("Orang Tua / Wali Siswa", 40f, yPosition, paint)
        canvas.drawText("Yang Membuat Pernyataan", 400f, yPosition, paint)

        yPosition += 60f
        canvas.drawText("__________________________", 40f, yPosition, paint)
        canvas.drawText("Nama Jelas dan Tanda Tangan", 40f, yPosition + 15f, paint)
        canvas.drawText("Materai Rp. 10.000,-", 400f, yPosition, paint)
        canvas.drawText(nama, 400f, yPosition + 15f, paint)

        pdfDocument.finishPage(page)

        // Menyimpan file PDF
        val directory = File(Environment.getExternalStorageDirectory().toString(), "Download")
        if (!directory.exists()) directory.mkdirs()
        val filePath = File(directory, "FormulirPendaftaran.pdf")

        try {
            pdfDocument.writeTo(FileOutputStream(filePath))
            Toast.makeText(this, "PDF saved in Download folder", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error generating PDF", Toast.LENGTH_LONG).show()
        } finally {
            pdfDocument.close()
        }
    }


}
